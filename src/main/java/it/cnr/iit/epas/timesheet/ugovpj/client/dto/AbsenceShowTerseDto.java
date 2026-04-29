/*
 * Copyright (C) 2026  Consiglio Nazionale delle Ricerche
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO per l'importazione via REST delle informazioni principali di un'assenza
 * presenti in ePAS.
 *
 * @author Cristian Lucchesi
 *
 */
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class AbsenceShowTerseDto {

  private Long id;
  private LocalDate date;
  private String code;
  private String description;
  private Long absenceTypeId;
  private Boolean isRealAbsence;
  private Integer justifiedTime;
  private String justifiedType;
  private String note;
  private String externalId;
  private String externalTypeId;
  private LocalDateTime updatedAt;

  private String extendedLabel;

  @JsonIgnore
  public String getLabel() {
    return String.format("%s - %s - (%s)", code, description, getJustifiedTimeFormatted());
  }

  @JsonIgnore
  public String getExtendedLabel() {
    if (extendedLabel != null) {
      return extendedLabel;
    } else {
      return getLabel();
    }
  }

  @JsonIgnore
  public String getJustifiedTimeFormatted() {
    if (justifiedTime == null) {
      return "00:00";
    }
    int hours = justifiedTime / 60;
    int minutes = justifiedTime % 60;
    return String.format("%02d:%02d", hours, minutes);
  }
}