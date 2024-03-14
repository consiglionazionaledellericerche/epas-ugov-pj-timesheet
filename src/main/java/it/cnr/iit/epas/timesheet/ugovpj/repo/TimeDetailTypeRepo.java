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
package it.cnr.iit.epas.timesheet.ugovpj.repo;

import it.cnr.iit.epas.timesheet.ugovpj.model.TimeDetailType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository per l'accesso e la gestione dei dati dei tipi di informazioni sui
 * tempi scambiate con il sistema di timesheet.
 *
 * @author Cristian Lucchesi
 */
public interface TimeDetailTypeRepo extends JpaRepository<TimeDetailType,Long> {
  //Empty interface
}