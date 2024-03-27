/*
 * Copyright (C) 2024  Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.cnr.iit.epas.timesheet.ugovpj.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import it.cnr.iit.epas.timesheet.ugovpj.client.EpasClient;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonDayShowTerseDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonMonthRecapDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonShowTerseDto;
import it.cnr.iit.epas.timesheet.ugovpj.config.TimesheetConfig;
import it.cnr.iit.epas.timesheet.ugovpj.exceptions.ConcurrentSyncException;
import it.cnr.iit.epas.timesheet.ugovpj.model.PersonTimeDetail;
import it.cnr.iit.epas.timesheet.ugovpj.repo.PersonTimeDetailRepo;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Servizio con i metodi che effettuano la sincronizzazione dei dati del tempo a lavoro
 * e delle assenze del personale sulle tabelle di ugog-pj-timesheet.
 *
 * @author Cristian Lucchesi
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SyncService {

  private final PersonTimeDetailRepo repo;
  private final TimeDetailTypeService typeService;

  private final EpasClient epasClient;
  private final TimesheetConfig timesheetConfig;

  private final MeterRegistry meterRegistry;

  private final SemaphoreService semaphore;

  /**
   * Sincronizza il dato del tempo a lavoro di una persona in un giorno specifico.
   */
  private Optional<PersonTimeDetail> syncPersonDayTimeAtWork(
      PersonShowTerseDto person, PersonDayShowTerseDto personDay, AtomicLong counter, boolean containsAbsences) {
    //Inserimento resoconto tempo a lavoro da timbrature
    val personTimeDetail = 
        PersonTimeDetail.builder()
          .id(counter.incrementAndGet())
          .date(personDay.getDate())
          .number(person.getNumber())
          .type(timesheetConfig.getStampingsType())
          .build();

    //Se il giorno contiene assenze allora non si prende il timeAtWork (che è il tempo giustificato)
    //ma il tempo da timbrature
    if (containsAbsences) {
      if (personDay.getStampingsTime() == 0) {
        log.debug("PersonDay id={}, date={} person.number={} ignored stampings time, it's zero", 
            personDay.getId(), personDay.getDate(), person.getNumber());
        return Optional.empty();
      }
      personTimeDetail.setMinutes(personDay.getStampingsTime());
    } else {
      if (personDay.getTimeAtWork() == 0) {
        log.debug("PersonDay id={} date={}, person.number={} ignored time at work, it's zero", 
            personDay.getId(), personDay.getDate(), person.getNumber());
        return Optional.empty();
      }
      personTimeDetail.setMinutes(personDay.getTimeAtWork());
    }

    repo.persistAndFlush(personTimeDetail);
    log.debug("Salvato tempo al lavoro personTimeDetail {}", personTimeDetail);
    return Optional.of(personTimeDetail);
  }

  /**
   * Sincronizza i dati delle assenze di una persona in un giorno specifico.
   */
  private List<PersonTimeDetail> syncPersonDayAbsences(
      PersonShowTerseDto person, PersonDayShowTerseDto personDay, AtomicLong counter) {
    Map<String, Integer> absenceMap = Maps.newHashMap();
    val timeDetailType = typeService.timeDetailTypes();
    personDay.getAbsences().stream()
    .filter(absence -> timeDetailType.contains(absence.getExternalTypeId()))
    .forEach(absence -> {
      if (absenceMap.containsKey(absence.getExternalTypeId())) {
        Integer previousValue = absenceMap.get(absence.getExternalTypeId());
        absenceMap.replace(absence.getExternalTypeId(), Optional.ofNullable(absence.getJustifiedTime()).orElse(0) + previousValue);
      } else {
        absenceMap.put(absence.getExternalTypeId(), Optional.ofNullable(absence.getJustifiedTime()).orElse(0));
      }
    });
    List<PersonTimeDetail> details = Lists.newArrayList();
    absenceMap.keySet().stream().forEach(absenceGroup -> {
      //Inserimento resoconto tempo giustificato per l'assenza
      val personTimeDetail = 
          PersonTimeDetail.builder()
            .id(counter.incrementAndGet())
            .date(personDay.getDate())
            .minutes(absenceMap.get(absenceGroup))
            .number(person.getNumber())
            .type(absenceGroup)
            .build();
      repo.persistAndFlush(personTimeDetail);
      log.debug("Salvata assenza personTimeDetail {}", personTimeDetail);
      details.add(personTimeDetail);
    });
    return details;
  }

  /**
   * Sincronizza i dati di presenze e assenze di una persona in un giorno specifico.
   */
  public List<PersonTimeDetail> syncPersonDay(
      PersonShowTerseDto person, PersonDayShowTerseDto personDay, AtomicLong counter) {
    log.trace("Sincronizzazione personDay {}", personDay);
    List<PersonTimeDetail> details = Lists.newArrayList();
    val containsAbsences = 
        personDay.getAbsences().stream().filter(a -> a.getIsRealAbsence()).count() > 0;
    //Inserimento resoconto tempo a lavoro da timbrature
    val timeAtWorkDetail = syncPersonDayTimeAtWork(person, personDay, counter, containsAbsences);
    if (timeAtWorkDetail.isPresent()) {
      details.add(timeAtWorkDetail.get());
    }
    //Inserimento delle assenze con externalGroupId rilevante, raggruppate per externalGroupId
    details.addAll(syncPersonDayAbsences(person, personDay, counter));
    return details;
  }

  /**
   * Sincronizzata i dati di presenze e assenze di una persona in un mese.
   */
  public List<PersonTimeDetail> syncPersonMonth(
      PersonMonthRecapDto monthRecap, Optional<LocalDate> notBefore,
      AtomicLong counter) {
    List<PersonTimeDetail> details = Lists.newArrayList();
    if (Strings.isNullOrEmpty(monthRecap.getPerson().getNumber())) {
      log.info("Ignorata persona {} perché senza matricola", monthRecap.getPerson());
      return details;
    }
    monthRecap.getPersonDays().forEach(personDay -> {
      if (notBefore.isEmpty() || !personDay.getDate().isBefore(notBefore.get())) {
        details.addAll(syncPersonDay(monthRecap.getPerson(), personDay, counter));
      }
    });
    return details;
  }

  /**
   * Sincronizzazione sulla tabella di frontiera del tempo a lavoro e assenze di tutti i dipendenti
   * di un ufficio nel mese indicato.
   */
  public List<PersonTimeDetail> syncOfficeMonth(
      long officeId, YearMonth yearMonth, Optional<LocalDate> notBefore,
      AtomicLong counter) {
    log.debug("Inizio sincronizzazione dell'ufficio id={} del {}, notBefore={}", 
        officeId, yearMonth, notBefore);
    long startTime = System.currentTimeMillis();
    Timer.Sample timer = Timer.start(meterRegistry);
    List<PersonTimeDetail> details = Lists.newArrayList();
      val monthRecaps = epasClient.getMonthRecap(officeId, yearMonth.getYear(), yearMonth.getMonthValue());
      monthRecaps.forEach(monthRecap -> {
        details.addAll(syncPersonMonth(monthRecap, notBefore, counter));
      });
    log.info("Terminata sincronizzazione dell'ufficio id={} del {} in {} secondi", 
        officeId, yearMonth, ((System.currentTimeMillis() - startTime) / 1000));
    timer.stop(Timer.builder("epas_sync_office_month")
        .description("Timer della sincronizzazione dei dati di un mese di un ufficio")
        .tag("officeId", String.valueOf(officeId))
        .tag("yearMonth", yearMonth.toString())
        .register(meterRegistry));
    return details;
  }

  /**
   * Sincronizza i dati di presenze ed assenze del mese passato come parametro per tuti i 
   * dipendenti presenti in ePAS.
   */
  public List<PersonTimeDetail> syncMonth(YearMonth yearMonth, Optional<LocalDate> notBefore, 
      AtomicLong counter) {
    List<PersonTimeDetail> details = Lists.newArrayList();
    val offices = epasClient.getActiveOffices(yearMonth.atDay(1));
    offices.forEach(office -> {
      details.addAll(syncOfficeMonth(office.getId(), yearMonth, notBefore, counter));
    });
    return details;
  }

  @Timed(value = "epas_sync_all_time", description = "Time taken to sync all the details")
  public List<PersonTimeDetail> syncAll() {

    // Acquisizione del semaforo per evitare più sincronizzazioni in contemporanea
    if (!semaphore.tryAcquire()) {
      throw new ConcurrentSyncException("syncAll already started");
    }
    List<PersonTimeDetail> details = Lists.newArrayList();
    long startTime = System.currentTimeMillis();
    try {
      LocalDate startingDate = LocalDate.now().minusDays(timesheetConfig.getDaysInThePast());
      log.info("Starting synchronization since {}", startingDate);
      YearMonth yearMonth = YearMonth.from(startingDate);
      if (timesheetConfig.isDeleteBeforeSyncAll()) {
        deleteAllPersonTimeDetails();
      }
      Long startingId = repo.findMaxid().orElse(0L);
      AtomicLong counter = new AtomicLong(startingId);
      while (!yearMonth.isAfter(YearMonth.from(LocalDate.now()))) {
        details.addAll(syncMonth(yearMonth, Optional.of(startingDate), counter));
        yearMonth = yearMonth.plusMonths(1);
      }
      Gauge.builder("epas_synch_details_count", () -> details.size())
        .description("Totale delle righe esportate durante la syncAll")
        .register(meterRegistry);
    } catch (Exception e) {
      log.warn("Problema durante la syncAll", e);
      throw e;
    } finally {
      //Rilascio del semaforo per permettere altre sincronizzazioni
      semaphore.release();
    }
    log.info("Synchronization ended in {} seconds", ((System.currentTimeMillis() - startTime) / 1000));
    return details;
  }

  public void deleteAllPersonTimeDetails() {
    repo.truncateTable();
    log.info("Cancellati tutti i resoconti di tempo a lavoro e assenze dei dipendenti");
  }

  /**
   * Invoca le stored procedure per il caricamento dei dati delle marcature
   * nella tabella definitiva.
   */
  public void loadDetails() {
    repo.loadDetails();
    repo.loadDetailsJob();
  }

  /**
   * Invoca le stored procedure per il caricamento dei dati delle marcature
   * nella tabella definitiva utilizzando native query.
   */
  public void loadDetailsNative() {
    repo.loadDetailsNative();
    repo.loadDetailsJobNative();
  }

}