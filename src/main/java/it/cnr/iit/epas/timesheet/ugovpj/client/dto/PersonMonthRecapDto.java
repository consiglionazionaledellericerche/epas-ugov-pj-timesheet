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
import java.util.List;
import lombok.Data;

/**
 * DTO per l'esportazione via REST delle informazioni del riepilogo mensile
 * assenze/presenze in un determinato mese.
 *
 * @author Cristian Lucchesi
 *
 */
@Data
public class PersonMonthRecapDto {

  private Integer year;
  private Integer month;
  private PersonShowTerseDto person;
  private int basedWorkingDays;

  private List<PersonDayShowTerseDto> personDays = Lists.newArrayList();

}
