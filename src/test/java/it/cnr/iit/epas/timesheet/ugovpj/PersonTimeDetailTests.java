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
package it.cnr.iit.epas.timesheet.ugovpj;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import it.cnr.iit.epas.timesheet.ugovpj.client.dto.AbsenceShowTerseDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonDayShowTerseDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonShowTerseDto;
import it.cnr.iit.epas.timesheet.ugovpj.model.PersonTimeDetail;
import it.cnr.iit.epas.timesheet.ugovpj.service.SyncService;
import jakarta.inject.Inject;

public class PersonTimeDetailTests extends SpringCommonTest {

  private final int FULLDAY_WORKING_MINUTES = 432;

  @Inject
  private SyncService syncService;

  private static AtomicLong counter;

  @BeforeAll
  public static void initCounter() {
      counter = new AtomicLong(1);
  }

  private PersonShowTerseDto getPerson() {
    return PersonShowTerseDto.builder()
        .number("9802")
        .build();
  }

  private PersonDayShowTerseDto getPersonDay() {
    return PersonDayShowTerseDto.builder()
        .date(LocalDate.now())
        .build();
  }

  @Test
  public void personDayWithOnlyStampings() {
    int workingTime = 400;
    PersonDayShowTerseDto pd = getPersonDay();
    pd.setTimeAtWork(workingTime);
    List<PersonTimeDetail> details = 
        syncService.syncPersonDay(getPerson(), pd, counter);
    assertThat(details).isNotNull();
    assertThat(details).asList().size().isEqualTo(1);
    assertThat(details.get(0).getMinutes()).isEqualTo(workingTime);
  }

  @Test
  public void holidayNoStampings() {
    PersonDayShowTerseDto pd = getPersonDay();
    pd.setTimeAtWork(0);
    AbsenceShowTerseDto absence = 
        AbsenceShowTerseDto.builder()
          .code("32")
          .date(LocalDate.now())
          .justifiedTime(FULLDAY_WORKING_MINUTES)
          .justifiedType("all_day")
          .externalTypeId("F")
          .isRealAbsence(true)
          .build();
    pd.getAbsences().add(absence);
    List<PersonTimeDetail> details = 
        syncService.syncPersonDay(getPerson(), pd, counter);
    assertThat(details).isNotNull();
    assertThat(details).asList().size().isEqualTo(1);
    assertThat(details.get(0).getMinutes()).isEqualTo(FULLDAY_WORKING_MINUTES);
    assertThat(details.get(0).getType()).isEqualTo("F");
  }

}