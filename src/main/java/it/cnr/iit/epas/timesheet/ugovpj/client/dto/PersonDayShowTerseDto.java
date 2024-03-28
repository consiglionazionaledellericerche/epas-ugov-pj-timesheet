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
package it.cnr.iit.epas.timesheet.ugovpj.client.dto;

import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO per esportare via JSON le informazioni principali di un PersonDay.
 *
 * @author Cristian Lucchesi
 *
 */
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class PersonDayShowTerseDto {

  private Long id;
  private LocalDate date;
  private int timeAtWork;
  private int difference;
  private int progressive;
  private int stampingsTime;
  private Integer decurtedMeal;
  private boolean isTicketAvailable;
  private boolean isHoliday;

  //private List<StampingShowTerseDto> stampings = Lists.newArrayList();

  @Builder.Default
  private List<AbsenceShowTerseDto> absences = Lists.newArrayList();

}