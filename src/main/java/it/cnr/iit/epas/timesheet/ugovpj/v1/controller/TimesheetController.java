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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.iit.epas.timesheet.ugovpj.config.TimesheetConfig;
import it.cnr.iit.epas.timesheet.ugovpj.repo.DetailLogRepo;
import it.cnr.iit.epas.timesheet.ugovpj.repo.PersonTimeDetailRepo;
import it.cnr.iit.epas.timesheet.ugovpj.repo.TimeDetailTypeRepo;
import it.cnr.iit.epas.timesheet.ugovpj.v1.ApiRoutes;
import it.cnr.iit.epas.timesheet.ugovpj.v1.dto.DetailLogDto;
import it.cnr.iit.epas.timesheet.ugovpj.v1.dto.DtoToEntityConverter;
import it.cnr.iit.epas.timesheet.ugovpj.v1.dto.PersonTimeDetailDto;
import it.cnr.iit.epas.timesheet.ugovpj.v1.dto.PersonTimeDetailMapper;
import it.cnr.iit.epas.timesheet.ugovpj.v1.dto.TimeDetailTypeDto;
import it.cnr.iit.epas.timesheet.ugovpj.v1.dto.TimesheetConfigDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller REST per la visualizzazione delle informazioni presenti
 * nelle tabelle Oracle di frontiera.
 *
 */
@Tag(
    name = "Timesheet Controller", 
    description = "Visualizzazione delle informazioni presenti nelle tabelle di frontiera (marcature, tipi, logs)")
@Slf4j
@RequestMapping(ApiRoutes.BASE_PATH + "/timesheet")
@RestController
@RequiredArgsConstructor
public class TimesheetController {

  private final TimeDetailTypeRepo timeDetailTypeRepo;
  private final PersonTimeDetailRepo personTimeDetailRepo;
  private final DetailLogRepo detailLogRepo;
  private final PersonTimeDetailMapper mapper;
  private final DtoToEntityConverter dtoToEntityConverter;
  private final TimesheetConfig timesheetConfig;

  @Operation(
      summary = "Visualizzazione dei tipi di marcatura presenti nella tabella IE_PJ_MARCATURE_TIPI.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restituiti i tipi di marcatura.")
  })
  @GetMapping("/timeDetailTypes")
  public ResponseEntity<List<TimeDetailTypeDto>> timeDetailTypes() {
    log.debug("Ricevuta richiesta estrazione lista dei tipi di presenza/assenza");
    val types = timeDetailTypeRepo.findAll();
    return ResponseEntity.ok().body(
        types.stream().map(mapper::convert).collect(Collectors.toList()));
  }

  @Operation(
      summary = "Visualizzazione delle informazioni di presenza/assenza di una persona, invididuata "
          + "tramite la sua matricola.",
      description = "Le informazioni sono estratte dalla tabella IE_PJ_MARCATURE ed i risultati sono paginati.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restituite le presenze/assenze della persona."),
      @ApiResponse(responseCode = "400", 
          description = "Numero di matricola non passato.",
          content = @Content)
  })
  @GetMapping("/personTimeDetails")
  public ResponseEntity<Page<PersonTimeDetailDto>> personTimeDetails(
      @NotNull @RequestParam("number") String number, 
      Pageable pageable) {
    log.debug("Ricevuta richiesta presenze/assenze persona matricola = {}", number);
    val details = personTimeDetailRepo.findByNumber(number, pageable).map(mapper::convert);
    return ResponseEntity.ok().body(details);
  }

  @Operation(
      summary = "Visualizzazione delle informazioni di presenza/assenza di tutte le persona",
      description = "Le informazioni sono estratte dalla tabella IE_PJ_MARCATURE ed i risultati sono paginati.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restituite le presenze/assenze presenti di tutte le persone.")
  })
  @GetMapping("/timeDetails")
  public ResponseEntity<Page<PersonTimeDetailDto>> timeDetails(
      Pageable pageable) {
    log.debug("Ricevuta richiesta presenze/assenze di tutte le persone");
    val details = personTimeDetailRepo.findAll(pageable).map(mapper::convert);
    return ResponseEntity.ok().body(details);
  }

  @PutMapping("/timeDetailTypes")
  public ResponseEntity<TimeDetailTypeDto> create(
      @NotNull @Valid @RequestBody TimeDetailTypeDto detailTypeDto) {
    log.debug("TimesheetController::create detailTypeDto = {}", detailTypeDto);
    val result = dtoToEntityConverter.createEntity(detailTypeDto);
    timeDetailTypeRepo.save(result);
    log.info("Creato Result {}", result);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.convert(result));
  }

  @Operation(
      summary = "Visualizzazione dei log delle procedure di carico di tutte le presenze/assenze",
      description = "I risultati mostrati sono quelli della tabella IE_PJ_MARCATURE_LOGS")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restituiti i log presenti.")
  })
  @GetMapping("/logs")
  public ResponseEntity<Page<DetailLogDto>> logs(
      Pageable pageable) {
    log.debug("Ricevuta richiesta visualizzazione log in tabella Oracle");
    val details = detailLogRepo.findAll(pageable).map(mapper::convert);
    return ResponseEntity.ok().body(details);
  }

  @Operation(
      summary = "Visualizzazione dei parametri relativi alla sincronizzazione dei dati")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restituiti i parametri presenti.")
  })
  @GetMapping("/config")
  public ResponseEntity<TimesheetConfigDto> config() {
    log.debug("Ricevuta richiesta visualizzazione configurazione parametri timesheet");
    return ResponseEntity.ok().body(mapper.convert(timesheetConfig));
  }

  @Operation(
      summary = "Visualizzazione del massimo id presente nella tabella delle marcature")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restituiti il massimo id della tabella IE_PJ_MARCATURE.")
  })
  @GetMapping("/maxId")
  public ResponseEntity<Long> maxId() {
    log.debug("Ricevuta richiesta max id tabelle marcature");
    return ResponseEntity.ok().body(personTimeDetailRepo.findMaxid().orElse(0L));
  }
}