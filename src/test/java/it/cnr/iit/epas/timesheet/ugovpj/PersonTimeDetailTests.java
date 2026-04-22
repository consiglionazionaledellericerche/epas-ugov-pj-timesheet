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

  private static final int FULLDAY_WORKING_MINUTES = 432;

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
    List<PersonTimeDetail> details = syncService.syncPersonDay(getPerson(), pd, counter);

    assertThat(details).isNotNull();
    assertThat(details).hasSize(1);
    assertThat(details.getFirst().getMinutes()).isEqualTo(workingTime);
  }

  @Test
  public void holidayNoStampings() {
    PersonDayShowTerseDto pd = getPersonDay();
    pd.setTimeAtWork(0);
    AbsenceShowTerseDto absence =
            AbsenceShowTerseDto.builder()
                    .code("32")
                    .description("Permesso")
                    .date(LocalDate.now())
                    .justifiedTime(FULLDAY_WORKING_MINUTES)
                    .justifiedType("all_day")
                    .externalTypeId("F")
                    .isRealAbsence(true)
                    .build();
    pd.getAbsences().add(absence);
    List<PersonTimeDetail> details = syncService.syncPersonDay(getPerson(), pd, counter);

    assertThat(details).isNotNull();
    assertThat(details).hasSize(1);
    assertThat(details.getFirst().getMinutes()).isEqualTo(FULLDAY_WORKING_MINUTES);
    assertThat(details.getFirst().getType()).isEqualTo("F");
    assertThat(details.getFirst().getAbsenceDescription()).isEqualTo("32 - Permesso");
  }

  @Test
  public void absencesWithSameExternalTypeAreAggregated() {
    PersonDayShowTerseDto pd = getPersonDay();

    pd.getAbsences().add(AbsenceShowTerseDto.builder()
            .code("A1")
            .description("Assenza 1")
            .date(LocalDate.now())
            .justifiedTime(60)
            .justifiedType("justified")
            .externalTypeId("F")
            .isRealAbsence(true)
            .build());

    pd.getAbsences().add(AbsenceShowTerseDto.builder()
            .code("A2")
            .description("Assenza 2")
            .date(LocalDate.now())
            .justifiedTime(30)
            .justifiedType("justified")
            .externalTypeId("F")
            .isRealAbsence(true)
            .build());

    List<PersonTimeDetail> details = syncService.syncPersonDay(getPerson(), pd, counter);

    assertThat(details).hasSize(1);
    assertThat(details.get(0).getType()).isEqualTo("F");
    assertThat(details.get(0).getMinutes()).isEqualTo(90);
    assertThat(details.get(0).getAbsenceDescription()).isEqualTo("A1 - Assenza 1; A2 - Assenza 2");
  }

  @Test
  public void absencesWithoutExternalTypeUseDefaultX() {
    PersonDayShowTerseDto pd = getPersonDay();

    pd.getAbsences().add(AbsenceShowTerseDto.builder()
            .code("A1")
            .description("Assenza 1")
            .date(LocalDate.now())
            .justifiedTime(20)
            .justifiedType("justified")
            .externalTypeId(null)
            .isRealAbsence(true)
            .build());

    pd.getAbsences().add(AbsenceShowTerseDto.builder()
            .code("A2")
            .description("Assenza 2")
            .date(LocalDate.now())
            .justifiedTime(40)
            .justifiedType("justified")
            .externalTypeId("")
            .isRealAbsence(true)
            .build());

    List<PersonTimeDetail> details = syncService.syncPersonDay(getPerson(), pd, counter);

    assertThat(details).hasSize(1);
    assertThat(details.get(0).getType()).isEqualTo("X");
    assertThat(details.get(0).getMinutes()).isEqualTo(60);
    assertThat(details.get(0).getAbsenceDescription()).isEqualTo("A1 - Assenza 1; A2 - Assenza 2");
  }

  @Test
  public void absencesWithDifferentExternalTypesAreNotMerged() {
    PersonDayShowTerseDto pd = getPersonDay();

    pd.getAbsences().add(AbsenceShowTerseDto.builder()
            .code("A1")
            .description("Assenza 1")
            .date(LocalDate.now())
            .justifiedTime(25)
            .justifiedType("justified")
            .externalTypeId("F")
            .isRealAbsence(true)
            .build());

    pd.getAbsences().add(AbsenceShowTerseDto.builder()
            .code("A2")
            .description("Assenza 2")
            .date(LocalDate.now())
            .justifiedTime(35)
            .justifiedType("justified")
            .externalTypeId("N")
            .isRealAbsence(true)
            .build());

    List<PersonTimeDetail> details = syncService.syncPersonDay(getPerson(), pd, counter);

    assertThat(details).hasSize(2);
    assertThat(details).extracting(PersonTimeDetail::getType).containsExactlyInAnyOrder("F", "N");
    assertThat(details).extracting(PersonTimeDetail::getMinutes).containsExactlyInAnyOrder(25, 35);
  }

  @Test
  public void absenceDescriptionIsTruncatedAt250Chars() {
    PersonDayShowTerseDto pd = getPersonDay();
    // Ogni label è "AX - Descrizione molto lunga per il test X" (~43 chars), 6 assenze => >250 chars
    for (int i = 1; i <= 6; i++) {
      pd.getAbsences().add(AbsenceShowTerseDto.builder()
              .code("A" + i)
              .description("Descrizione molto lunga per il test " + i)
              .date(LocalDate.now())
              .justifiedTime(10)
              .justifiedType("justified")
              .externalTypeId("F")
              .isRealAbsence(true)
              .build());
    }

    List<PersonTimeDetail> details = syncService.syncPersonDay(getPerson(), pd, counter);

    assertThat(details).hasSize(1);
    assertThat(details.get(0).getAbsenceDescription()).hasSizeLessThanOrEqualTo(250);
  }

  @Test
  public void absenceWithNullJustifiedTimeCountsAsZero() {
    PersonDayShowTerseDto pd = getPersonDay();
    pd.getAbsences().add(AbsenceShowTerseDto.builder()
            .code("A1")
            .description("Assenza senza tempo")
            .date(LocalDate.now())
            .justifiedTime(null)
            .justifiedType("justified")
            .externalTypeId("F")
            .isRealAbsence(true)
            .build());

    List<PersonTimeDetail> details = syncService.syncPersonDay(getPerson(), pd, counter);

    assertThat(details).hasSize(1);
    assertThat(details.get(0).getMinutes()).isEqualTo(0);
  }

  @Test
  public void absenceWithJustifiedTypeNothingIsIgnored() {
    PersonDayShowTerseDto pd = getPersonDay();
    pd.setTimeAtWork(300);
    pd.getAbsences().add(AbsenceShowTerseDto.builder()
            .code("A1")
            .description("Assenza nothing")
            .date(LocalDate.now())
            .justifiedTime(60)
            .justifiedType("nothing")
            .externalTypeId("F")
            .isRealAbsence(false)
            .build());

    List<PersonTimeDetail> details = syncService.syncPersonDay(getPerson(), pd, counter);

    assertThat(details).hasSize(1);
    assertThat(details.get(0).getType()).isEqualTo("N");
  }

  @Test
  public void missionWithExternalTypeTIsIncludedEvenIfNotRealAbsence() {
    PersonDayShowTerseDto pd = getPersonDay();
    pd.getAbsences().add(AbsenceShowTerseDto.builder()
            .code("92M")
            .description("Missione oraria")
            .date(LocalDate.now())
            .justifiedTime(120)
            .justifiedType("specified_minutes")
            .externalTypeId("T")
            .isRealAbsence(false)
            .build());

    List<PersonTimeDetail> details = syncService.syncPersonDay(getPerson(), pd, counter);

    assertThat(details).hasSize(1);
    assertThat(details.get(0).getType()).isEqualTo("T");
    assertThat(details.get(0).getMinutes()).isEqualTo(120);
  }

}