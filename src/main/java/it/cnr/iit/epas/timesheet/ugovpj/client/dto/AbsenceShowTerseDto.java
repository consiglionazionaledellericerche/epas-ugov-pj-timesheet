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
import lombok.EqualsAndHashCode;

/**
 * DTO per l'importazione via REST delle informazioni principali di un'assenza
 * presenti in ePAS.
 *
 * @author Cristian Lucchesi
 *
 */
@Data
@EqualsAndHashCode(of = "id")
public class AbsenceShowTerseDto {

  private Long id;
  private LocalDate date;
  private String code;
  private Long absenceTypeId;
  private Integer justifiedTime;
  private String justifiedType;
  private String note;
  private String externalId;
  private String externalTypeId;
  private LocalDateTime updatedAt;

}