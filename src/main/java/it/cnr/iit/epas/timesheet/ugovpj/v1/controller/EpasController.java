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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.iit.epas.timesheet.ugovpj.client.EpasClient;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.OfficeDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonMonthRecapDto;
import it.cnr.iit.epas.timesheet.ugovpj.v1.ApiRoutes;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST per mostrare alcune informazioni presenti in ePAS.
 *
 * @author Cristian Lucchesi
 */
@Tag(
    name = "ePAS Controller", 
    description = "Visualizzazione di alcune informazioni presenti in ePAS (lista uffici e rendiconti mensili")
@Slf4j
@RequestMapping(ApiRoutes.BASE_PATH + "/epas")
@RestController
@RequiredArgsConstructor
public class EpasController {

  private final EpasClient epasClient;

  @Operation(
      summary = "Visualizzazione della lista degli uffici attivi presenti in ePAS.",
      description = "La lista degli uffici attivi viene prelevata via REST da ePAS e può condizionata "
          + "indicando la data in cui gli uffici devono essere attivi.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restituita la lista degli uffici.")
  })
  @GetMapping("/offices")
  public ResponseEntity<List<OfficeDto>> offices(
      @RequestParam("atDate")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      Optional<LocalDate> atDate) {
    log.debug("Ricevuta richiesta estrazione lista di uffici da ePAS alla data {}", atDate.orElse(LocalDate.now()));
    val offices = epasClient.getActiveOffices(atDate.orElse(LocalDate.now()));
    return ResponseEntity.ok().body(offices);
  }

  @Operation(
      summary = "Visualizzazione del riepilogo mensile di un ufficio individuato tramite il suo id in ePAS.",
      description = "Il riepilogo viene prelevato via RST da REST.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", 
          description = "Restituito il riepilogo mensile con tutte le presenze/assenza di tutto il personale "
              + "del ufficio."),
      @ApiResponse(responseCode = "400", 
          description = "Non passato uno dei tre parametri obbligatori: officeId, year, month.",
          content = @Content)
  })
  @GetMapping("/officeMonthRecap")
  public ResponseEntity<List<PersonMonthRecapDto>> officeMonthRecap(
      @NotNull @RequestParam("officeId") Long officeId, @NotNull @RequestParam("year") int year, 
      @NotNull @RequestParam("month") int month) {
    log.debug("Ricevuta richiesta visualizzazione riepilogo mensile per ufficio id={} {}/{}", 
        officeId, month, year);
    val monthRecap = epasClient.getMonthRecap(officeId, year, month);
    return ResponseEntity.ok().body(monthRecap);
  }

}