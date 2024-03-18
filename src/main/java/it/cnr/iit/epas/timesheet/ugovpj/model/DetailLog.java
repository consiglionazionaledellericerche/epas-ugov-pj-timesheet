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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Modello per il mapping della Tabella Oracle di UGOV-Pj-Timesheet che contiene i tipi
 * con i log del passaggio dei dati.
 *
 * @author Cristian Lucchesi
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
@Entity
@Table(name = "IE_PJ_MARCATURE_LOGS")
public class DetailLog {

  @Id
  @Column(name = "ID_LOG")
  private Long id;

  @NotNull
  @Column(name = "DATA")
  private LocalDate date;

  @NotNull
  @Column(name = "ESITO")
  private String outcome;

  @Column(name = "NUM_MARCATURE")
  private Long numberOfDetails;

}