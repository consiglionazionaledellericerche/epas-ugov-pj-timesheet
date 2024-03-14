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
package it.cnr.iit.epas.timesheet.ugovpj.controller.v1;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.cnr.iit.epas.timesheet.ugovpj.client.dto.v1.PersonTimeDetailDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.v1.PersonTimeDetailMapper;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.v1.TimeDetailTypeDto;
import it.cnr.iit.epas.timesheet.ugovpj.repo.PersonTimeDetailRepo;
import it.cnr.iit.epas.timesheet.ugovpj.repo.TimeDetailTypeRepo;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping(ApiRoutes.BASE_PATH + "/timesheet")
@RestController
@RequiredArgsConstructor
public class TimesheetController {

  private final TimeDetailTypeRepo timeDetailTypeRepo;
  private final PersonTimeDetailRepo personTimeDetailRepo;
  private final PersonTimeDetailMapper mapper;
  
  @GetMapping("/timeDetailTypes")
  public ResponseEntity<List<TimeDetailTypeDto>> timeDetailTypes() {
    log.debug("Ricevuta richiesta estrazione lista dei tipi di presenza/assenza");
    val types = timeDetailTypeRepo.findAll();
    return ResponseEntity.ok().body(
        types.stream().map(mapper::convert).collect(Collectors.toList()));
  }

  @GetMapping("/personTimeDetails")
  public ResponseEntity<Page<PersonTimeDetailDto>> personTimeDetails(
      @NotNull @RequestParam("number") String number, Pageable pageable) {
    log.debug("Ricevuta richiesta presenze/assenze persona matricola = {}", number);
    val details = personTimeDetailRepo.findByNumber(number, pageable).map(mapper::convert);
    return ResponseEntity.ok().body(details);
  }

  @GetMapping("/timeDetails")
  public ResponseEntity<Page<PersonTimeDetailDto>> timeDetails(
      Pageable pageable) {
    log.debug("Ricevuta richiesta presenze/assenze di tutte le persone");
    val details = personTimeDetailRepo.findAll(pageable).map(mapper::convert);
    return ResponseEntity.ok().body(details);
  }
}