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
package it.cnr.iit.epas.timesheet.ugovpj.config;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;

/**
 * Contenitore dei parametri di configurazione per l'integrazione dei dati
 * tra ePAS e UGov-PJ-Timesheet.
 *
 * @author Cristian Lucchesi
 *
 */
@Data
@EqualsAndHashCode
@ToString
@Configuration
@ConfigurationProperties(prefix = "timesheet")
public class TimesheetConfig {

  private String stampingsType = "N";
  private List<String> allDayPresenceCodes = 
      Lists.newArrayList("assign_all_day", "complete_day_and_add_overtime");
  private int daysInThePast = 90;
  private boolean deleteBeforeSyncAll = true;

}