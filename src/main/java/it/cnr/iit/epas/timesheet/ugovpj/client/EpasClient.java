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
package it.cnr.iit.epas.timesheet.ugovpj.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.OfficeDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonMonthRecapDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Client REST per l'accesso alla informazioni presenti nel server di ePAS v2
 *
 * @author Cristian Lucchesi
 *
 */
@Headers({"Content-Type: application/json"})
public interface EpasClient {

  @RequestLine("GET /rest/v3/offices/all?atDate={atDate}")
  public List<OfficeDto> getActiveOffices(
      @Param("atDate")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate atDate);
  
  @RequestLine("GET /rest/v3/persondays/getmonthsituationbyoffice?id={id}&year={year}&month={month}")
  public List<PersonMonthRecapDto> getMonthRecap(
      @Param("id")long id, @Param("year")int year, @Param("month") int month);
}