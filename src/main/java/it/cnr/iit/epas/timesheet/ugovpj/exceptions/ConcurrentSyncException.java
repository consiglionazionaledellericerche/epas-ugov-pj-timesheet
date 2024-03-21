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
package it.cnr.iit.epas.timesheet.ugovpj.exceptions;

import lombok.NoArgsConstructor;

/**
 * Eccezione sollevata in caso di tentativo di avvio concorrente
 * di più richieste di sincronizzazione.
 *
 * @author Cristian Lucchesi
 */
@NoArgsConstructor
public class ConcurrentSyncException extends RuntimeException {

  private static final long serialVersionUID = 5627120225969253940L;

  public ConcurrentSyncException(String message) {
    super(message);
  }

}