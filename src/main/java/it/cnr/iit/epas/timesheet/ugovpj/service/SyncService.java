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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.cnr.iit.epas.timesheet.ugovpj.client.EpasClient;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonDayShowTerseDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonMonthRecapDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonShowTerseDto;
import it.cnr.iit.epas.timesheet.ugovpj.config.TimesheetConfig;
import it.cnr.iit.epas.timesheet.ugovpj.model.PersonTimeDetail;
import it.cnr.iit.epas.timesheet.ugovpj.model.TimeDetailType;
import it.cnr.iit.epas.timesheet.ugovpj.repo.PersonTimeDetailRepo;
import it.cnr.iit.epas.timesheet.ugovpj.repo.TimeDetailTypeRepo;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
  private final TimeDetailTypeRepo typeRepo;
  
  private final EpasClient epasClient;
  private final TimesheetConfig timesheetConfig;

  /**
   * Sincronizza il dato del tempo a lavoro di una persona in un giorno specifico.
   */
  private Optional<PersonTimeDetail> syncPersonDayTimeAtWork(PersonShowTerseDto person, PersonDayShowTerseDto personDay) {
    //Inserimento resoconto tempo a lavoro da timbrature
    if (personDay.getTimeAtWork() == 0) {
      log.trace("PersonDay id={} person.number={} ignored time at work, it's zero", 
          personDay.getId(), person.getNumber());
      return Optional.empty();
    }
    val personTimeDetail = 
        PersonTimeDetail.builder()
          .date(personDay.getDate()).minutes(personDay.getTimeAtWork())
          .number(person.getNumber())
          .type(timesheetConfig.getStampingsType())
          .build();
    repo.save(personTimeDetail);
    return Optional.of(personTimeDetail);
  }

  /**
   * Sincronizza i dati delle assenze di una persona in un giorno specifico.
   */
  private List<PersonTimeDetail> syncPersonDayAbsences(PersonShowTerseDto person, PersonDayShowTerseDto personDay) {
    Map<String, Integer> absenceMap = Maps.newHashMap();
    val timeDetailType = timeDetailTypes();
    personDay.getAbsences().stream()
    .filter(absence -> timeDetailType.contains(absence.getExternalTypeId()))
    .forEach(absence -> {
      if (absenceMap.containsKey(absence.getExternalTypeId())) {
        Integer previousValue = absenceMap.get(absence.getExternalTypeId());
        absenceMap.replace(absence.getExternalTypeId(), absence.getJustifiedTime() + previousValue);
      } else {
        absenceMap.put(absence.getExternalTypeId(), absence.getJustifiedTime());
      }
    });
    List<PersonTimeDetail> details = Lists.newArrayList();
    absenceMap.keySet().stream().forEach(absenceGroup -> {
      //Inserimento resoconto tempo giustificato per l'assenza
      val personTimeDetail = 
          PersonTimeDetail.builder()
            .date(personDay.getDate()).minutes(absenceMap.get(absenceGroup))
            .number(person.getNumber())
            .type(absenceGroup)
            .build();
      repo.save(personTimeDetail);
      details.add(personTimeDetail);
    });
    return details;
  }

  /**
   * Sincronizza i dati di presenze e assenze di una persona in un giorno specifico.
   */
  public List<PersonTimeDetail> syncPersonDay(PersonShowTerseDto person, PersonDayShowTerseDto personDay) {
    log.trace("Sincronizzazione personDay {}", personDay);
    List<PersonTimeDetail> details = Lists.newArrayList();
    //Inserimento resoconto tempo a lavoro da timbrature
    val timeAtWorkDetail = syncPersonDayTimeAtWork(person, personDay);
    if (timeAtWorkDetail.isPresent()) {
      details.add(timeAtWorkDetail.get());
    }
    //Inserimento delle assenze con externalGroupId rilevante, raggruppate per externalGroupId
    details.addAll(syncPersonDayAbsences(person, personDay));
    return details;
  }

  /**
   * Sincronizzata i dati di presenze e assenze di una persona in un mese.
   */
  public List<PersonTimeDetail> syncPersonMonth(PersonMonthRecapDto monthRecap, Optional<LocalDate> notBefore) {
    List<PersonTimeDetail> details = Lists.newArrayList();
    if (Strings.isNullOrEmpty(monthRecap.getPerson().getNumber())) {
      log.info("Ignorata persona {} perché senza matricola", monthRecap.getPerson());
      return details;
    }
    monthRecap.getPersonDays().forEach(personDay -> {
      if (notBefore.isEmpty() || !personDay.getDate().isBefore(notBefore.get())) {
        details.addAll(syncPersonDay(monthRecap.getPerson(), personDay));
      }
    });
    return details;
  }

  /**
   * Sincronizzazione sulla tabella di frontiera del tempo a lavoro e assenze di tutti i dipendenti
   * di un ufficio nel mese indicato.
   */
  public List<PersonTimeDetail> syncOfficeMonth(
      long officeId, YearMonth yearMonth, Optional<LocalDate> notBefore) {
    log.debug("Inizio sincronizzazione dell'ufficio id={} del {}, notBefore={}", 
        officeId, yearMonth, notBefore);
    long startTime = System.currentTimeMillis();
    List<PersonTimeDetail> details = Lists.newArrayList();
      val monthRecaps = epasClient.getMonthRecap(officeId, yearMonth.getYear(), yearMonth.getMonthValue());
      monthRecaps.forEach(monthRecap -> {
        details.addAll(syncPersonMonth(monthRecap, notBefore));
      });
    log.info("Terminata sincronizzazione dell'ufficio id={} del {} in {} secondi", 
        officeId, yearMonth, ((System.currentTimeMillis() - startTime) / 1000));
    return details;
  }

  /**
   * Sincronizza i dati di presenze ed assenze del mese passato come parametro per tuti i 
   * dipendenti presenti in ePAS.
   */
  public List<PersonTimeDetail> syncMonth(YearMonth yearMonth, Optional<LocalDate> notBefore) {
    List<PersonTimeDetail> details = Lists.newArrayList();
    val offices = epasClient.getActiveOffices(yearMonth.atDay(1));
    offices.forEach(office -> {
      syncOfficeMonth(office.getId(), yearMonth, notBefore);
    });
    return details;
  }

  public List<PersonTimeDetail> syncAll() {
    List<PersonTimeDetail> details = Lists.newArrayList();
    LocalDate startingDate = LocalDate.now().minusDays(timesheetConfig.getDaysInThePast());
    long startTime = System.currentTimeMillis();
    log.info("Starting synchronization since {}", startingDate);
    YearMonth yearMonth = YearMonth.from(startingDate);
    while (!yearMonth.isAfter(YearMonth.from(LocalDate.now()))) {
      syncMonth(yearMonth, Optional.of(startingDate));
      yearMonth = yearMonth.plusMonths(1);
    }
    log.info("Synchronization ended in {} seconds", ((System.currentTimeMillis() - startTime) / 1000));
    return details;
  }

  private Set<String> timeDetailTypes() {
    return typeRepo.findAll().stream().map(TimeDetailType::getCode).collect(Collectors.toSet());
  }

}