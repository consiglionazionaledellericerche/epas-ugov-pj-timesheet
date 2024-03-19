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
package it.cnr.iit.epas.timesheet.ugovpj.v1.controller;

import it.cnr.iit.epas.timesheet.ugovpj.client.EpasClient;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.OfficeDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonMonthRecapDto;
import it.cnr.iit.epas.timesheet.ugovpj.service.CachingService;
import it.cnr.iit.epas.timesheet.ugovpj.service.SyncService;
import it.cnr.iit.epas.timesheet.ugovpj.v1.ApiRoutes;
import it.cnr.iit.epas.timesheet.ugovpj.v1.dto.PersonTimeDetailDto;
import it.cnr.iit.epas.timesheet.ugovpj.v1.dto.PersonTimeDetailMapper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST con varie operazioni di amministrazione.
 *
 * @author Cristian Lucchesi
 */
@Slf4j
@RequestMapping(ApiRoutes.BASE_PATH + "/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {

  private final EpasClient epasClient;
  private final SyncService syncService;
  private final CachingService cachingService;
  private final PersonTimeDetailMapper mapper;

  @GetMapping("/offices")
  public ResponseEntity<List<OfficeDto>> offices(
      @RequestParam("atDate")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      Optional<LocalDate> atDate) {
    log.debug("Ricevuta richiesta estrazione lista di uffici da ePAS alla data {}", atDate.orElse(LocalDate.now()));
    val offices = epasClient.getActiveOffices(atDate.orElse(LocalDate.now()));
    return ResponseEntity.ok().body(offices);
  }

  @GetMapping("/officeMonthRecap")
  public ResponseEntity<List<PersonMonthRecapDto>> officeMonthRecap(
      @RequestParam("officeId") Long officeId, @RequestParam("year") int year, @RequestParam("month") int month) {
    log.debug("Ricevuta richiesta visualizzazione riepilogo mensile per ufficio id={} {}/{}", 
        officeId, month, year);
    val monthRecap = epasClient.getMonthRecap(officeId, year, month);
    return ResponseEntity.ok().body(monthRecap);
  }

  @PostMapping("/officeMonthRecap")
  public ResponseEntity<List<PersonTimeDetailDto>> syncOfficeMonthRecap(
      @RequestParam("officeId") Long officeId, @RequestParam("year") int year, @RequestParam("month") int month) {
    log.info("Ricevuta richiesta aggiornamento del riepilogo mensile per ufficio id={} {}/{}", officeId, month, year);
    val details = syncService.syncOfficeMonth(officeId, YearMonth.of(year, month), Optional.empty());
    return ResponseEntity.ok().body(details.stream().map(mapper::convert).collect(Collectors.toList()));
  }

  @PostMapping("/syncAll")
  public ResponseEntity<Integer> syncAll() {
    log.info("Ricevuta richiesta aggiornamento dei riepilogi di tutti gli uffici");
    val details = syncService.syncAll();
    return ResponseEntity.ok().body(details.size());
  }

  @DeleteMapping("/deleteAll")
  public ResponseEntity<Void> deleteAll() {
    log.debug("Richiesta eliminazione di tutti i dettagli del tempo a lavoro e assenze personale");
    syncService.deleteAllPersonTimeDetails();
    return ResponseEntity.ok().build();
  }

  @PostMapping("/loadDetails")
  public ResponseEntity<Void> loadDetails() {
    log.debug("Richiesta caricamento definitivo dati tramite stored procedure");
    syncService.loadDetails();
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/evictCaches")
  public ResponseEntity<Void> evictCaches() {
    log.debug("Richiesta evict di tutte le cache");
    cachingService.evictAllCaches();
    return ResponseEntity.ok().build();
  }
}