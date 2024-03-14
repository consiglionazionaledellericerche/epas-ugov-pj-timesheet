/*
 * Copyright (C) 2024 Consiglio Nazionale delle Ricerche
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
package it.cnr.iit.epas.timesheet.ugovpj.v1.dto;

import org.springframework.stereotype.Component;

import it.cnr.iit.epas.timesheet.ugovpj.model.TimeDetailType;
import lombok.RequiredArgsConstructor;

/**
 * Classe di utilità per convertire un DTO nella corrispondente Entity.
 *
 */
@RequiredArgsConstructor
@Component
public class DtoToEntityConverter {

  private final PersonTimeDetailMapper mapper;

  /**
   * Crea una nuova Entity Result a partire dai dati del DTO.
   */
  public TimeDetailType createEntity(TimeDetailTypeDto dto) {
    TimeDetailType type = new TimeDetailType();
    mapper.update(type, dto);
    return type;
  }

}