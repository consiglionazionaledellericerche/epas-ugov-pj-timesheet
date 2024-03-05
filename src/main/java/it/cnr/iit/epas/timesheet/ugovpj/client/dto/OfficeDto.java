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

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;

/**
 * Dati esportati in Json per l'Ufficio.
 *
 * @author Cristian Lucchesi
 *
 */
@ToString
@Data
public class OfficeDto {

  private Long id;
  private Long perseoId;
  private String name;

  //Codice della sede, per esempio per la sede di Pisa è "044000"
  private String code;

  //sedeId, serve per l'invio degli attestati, per esempio per la sede di Pisa è "223400"
  private String codeId;
  private String address;
  private LocalDate joiningDate;
  private LocalDate beginDate;
  private LocalDate endDate;

  private Long instituteId;
  private boolean headQuarter = false;
  private LocalDateTime updatedAt;

}