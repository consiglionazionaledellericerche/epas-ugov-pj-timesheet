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

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.iit.epas.timesheet.ugovpj.repo.PersonTimeDetailRepo;
import it.cnr.iit.epas.timesheet.ugovpj.service.CachingService;
import it.cnr.iit.epas.timesheet.ugovpj.service.SyncService;
import it.cnr.iit.epas.timesheet.ugovpj.v1.ApiRoutes;
import it.cnr.iit.epas.timesheet.ugovpj.v1.dto.PersonTimeDetailDto;
import it.cnr.iit.epas.timesheet.ugovpj.v1.dto.PersonTimeDetailMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller REST con varie operazioni di amministrazione.
 *
 * @author Cristian Lucchesi
 */
@Tag(
    name = "Admin Controller", 
    description = "Avvio delle operazioni di sincronizzazione e altre funzioni di amministrazione del servizio")
@Slf4j
@RequestMapping(ApiRoutes.BASE_PATH + "/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {

  private final SyncService syncService;
  private final PersonTimeDetailRepo personTimeDetailRepo;
  private final CachingService cachingService;
  private final PersonTimeDetailMapper mapper;

  @Operation(
      summary = "Avvio della sincronizzazione dei dati di tutti gli uffici.",
      description = "La sincronizzazione non svuota la tabella IE_PJ_MARCATURE, eventualmente svuotarla con l'apposito"
          + "endpoint REST. Il periodo dipende dalla configurazione.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Dati sincronizzati e restituito il numero di riepiloghi inseriti"),
      @ApiResponse(responseCode = "409", 
      description = "Operazione non effettuata perché già in corso una sincronizzazione.")
  })
  @PostMapping("/syncAll")
  public ResponseEntity<Integer> syncAll() {
    log.info("Ricevuta richiesta aggiornamento dei riepilogi di tutti gli uffici");
    val details = syncService.syncAll();
    return ResponseEntity.ok().body(details.size());
  }

  @Operation(
      summary = "Cancellata tutti i dati delle presenze/assenza, svuotando la tabella IE_PJ_MARCATURE.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Dati cancellati correttamente")
  })
  @DeleteMapping("/deleteAll")
  public ResponseEntity<Void> deleteAll() {
    log.debug("Richiesta eliminazione di tutti i dettagli del tempo a lavoro e assenze personale");
    syncService.deleteAllPersonTimeDetails();
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Effettua il caricamento definitivo dei dati delle presenze/assenza, invocando le apposite "
          + "stored procedures.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Stored procedure di caricamento definitivo dei dati invocate correttamente")
  })
  @PostMapping("/loadDetails")
  public ResponseEntity<Void> loadDetails() {
    log.debug("Richiesta caricamento definitivo dati tramite stored procedure");
    syncService.loadDetails();
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Effettua il caricamento definitivo dei dati delle presenze/assenza, invocando le apposite "
          + "stored procedures (in modo SQL nativo, senza utilizzare Spring Data).")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Stored procedure di caricamento definitivo dei dati invocate correttamente")
  })
  @PostMapping("/loadDetailsNative")
  public ResponseEntity<Void> loadDetailsNative() {
    log.debug("Richiesta caricamento definitivo dati tramite stored procedure (native)");
    syncService.loadDetailsNative();
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Effettua la sincronizzazione dei dati di presenza/assenza di un singolo ufficio "
          + "in un singolo mese.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Inseriti i dati di presenze/assenza e mostrato il dettaglio dei dati inseriti"),
      @ApiResponse(responseCode = "400", 
          description = "Non passato uno dei tre parametri obbligatori: officeId, year, month.",
          content = @Content)
  })
  @PostMapping("/officeMonthRecap")
  public ResponseEntity<List<PersonTimeDetailDto>> syncOfficeMonthRecap(
      @RequestParam("officeId") Long officeId, @RequestParam("year") int year, @RequestParam("month") int month) {
    log.info("Ricevuta richiesta aggiornamento del riepilogo mensile per ufficio id={} {}/{}", officeId, month, year);
    Long startingId = personTimeDetailRepo.findMaxid().orElse(0L);
    val details = 
        syncService.syncOfficeMonth(
            officeId, YearMonth.of(year, month), Optional.empty(), new AtomicLong(startingId));
    return ResponseEntity.ok().body(details.stream().map(mapper::convert).collect(Collectors.toList()));
  }

  @Operation(
      summary = "Svuota le cache utilizzate, in particolare quella dei tipi di marcatura.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Cache svuotata correttamente")
  })
  @DeleteMapping("/evictCaches")
  public ResponseEntity<Void> evictCaches() {
    log.debug("Richiesta evict di tutte le cache");
    cachingService.evictAllCaches();
    return ResponseEntity.ok().build();
  }

}