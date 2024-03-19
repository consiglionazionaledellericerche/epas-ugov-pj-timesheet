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
package it.cnr.iit.epas.timesheet.ugovpj.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Modello per il mapping della Tabella Oracle di UGOV-Pj-Timesheet.
 *
 * @author Cristian Lucchesi
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
@Entity
@Table(name = "IE_PJ_MARCATURE")
public class PersonTimeDetail {

  @Id
  @Column(name = "ID_IE_PJ_MARCATURE")
  private Long id;

  @NotNull
  @Column(name = "DATA")
  private LocalDate date;

  @NotNull
  @Column(name = "MATRICOLA")
  private String number;

  @NotNull
  @Column(name = "MARCATURA")
  private int minutes;

  @Builder.Default
  @Column(name = "FL_CONSOLIDATA")
  private int permanent = 1;

  @NotNull
  @Column(name = "TIPO_MARCATURA")
  private String type;

}