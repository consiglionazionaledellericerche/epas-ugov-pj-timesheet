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

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import it.cnr.iit.epas.timesheet.ugovpj.model.PersonTimeDetail;
import it.cnr.iit.epas.timesheet.ugovpj.model.TimeDetailType;

/**
 * Mapping dei dati delle Entity nei rispettivi DTO.
 *
 */
@Mapper(componentModel = "spring")
public interface PersonTimeDetailMapper {

  PersonTimeDetailDto convert(PersonTimeDetail detail);

  TimeDetailTypeDto convert(TimeDetailType type);

  void update(@MappingTarget TimeDetailType type, TimeDetailTypeDto dto);
}