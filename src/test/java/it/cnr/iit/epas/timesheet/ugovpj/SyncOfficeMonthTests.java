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
package it.cnr.iit.epas.timesheet.ugovpj;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonDayShowTerseDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonMonthRecapDto;
import it.cnr.iit.epas.timesheet.ugovpj.client.dto.PersonShowTerseDto;
import it.cnr.iit.epas.timesheet.ugovpj.service.SyncService;
import jakarta.inject.Inject;

/**
 * Test di integrazione per verificare che syncOfficeMonth persista correttamente
 * tutti i record anche quando si attraversano più batch di flush (batchSize=5 in test).
 */
public class SyncOfficeMonthTests extends SpringCommonTest {

  private static final YearMonth TEST_YEAR_MONTH = YearMonth.of(2026, 1);

  @Inject
  private SyncService syncService;

  /**
   * Costruisce un PersonMonthRecapDto con numDays giorni lavorativi,
   * ciascuno con minutesPerDay minuti di lavoro da timbrature.
   */
  private PersonMonthRecapDto monthRecap(String number, int numDays, int minutesPerDay) {
    PersonMonthRecapDto recap = new PersonMonthRecapDto();
    recap.setPerson(PersonShowTerseDto.builder().number(number).build());
    for (int i = 1; i <= numDays; i++) {
      PersonDayShowTerseDto pd = PersonDayShowTerseDto.builder()
          .date(LocalDate.of(2026, 1, i))
          .build();
      pd.setTimeAtWork(minutesPerDay);
      recap.getPersonDays().add(pd);
    }
    return recap;
  }

  @Test
  void syncOfficeMonthPersistsRecordsWithinOneBatch() {
    // 3 persone × 1 giorno = 3 record (< batchSize=5, nessun flush intermedio)
    when(epasClient.getMonthRecap(anyLong(), anyInt(), anyInt())).thenReturn(List.of(
        monthRecap("0001", 1, 480),
        monthRecap("0002", 1, 480),
        monthRecap("0003", 1, 480)
    ));

    syncService.syncOfficeMonth(1L, TEST_YEAR_MONTH, Optional.empty(), new AtomicLong(0));

    assertThat(repo.count()).isEqualTo(3);
  }

  @Test
  void syncOfficeMonthPersistsAllRecordsAcrossMultipleBatches() {
    // 3 persone × 4 giorni = 12 record (> batchSize=5, trigger di 2 flush intermedi)
    // Verifica che entityManager.clear() non perda record già persistiti
    when(epasClient.getMonthRecap(anyLong(), anyInt(), anyInt())).thenReturn(List.of(
        monthRecap("0001", 4, 480),
        monthRecap("0002", 4, 480),
        monthRecap("0003", 4, 480)
    ));

    syncService.syncOfficeMonth(1L, TEST_YEAR_MONTH, Optional.empty(), new AtomicLong(0));

    assertThat(repo.count()).isEqualTo(12);
  }

  @Test
  void syncOfficeMonthIgnoresPersonWithEmptyNumber() {
    when(epasClient.getMonthRecap(anyLong(), anyInt(), anyInt())).thenReturn(List.of(
        monthRecap("", 3, 480)
    ));

    syncService.syncOfficeMonth(1L, TEST_YEAR_MONTH, Optional.empty(), new AtomicLong(0));

    assertThat(repo.count()).isEqualTo(0);
  }

  @Test
  void syncOfficeMonthIgnoresPersonWithNumberTooLong() {
    // matricola di 7 caratteri > max consentito di 6
    when(epasClient.getMonthRecap(anyLong(), anyInt(), anyInt())).thenReturn(List.of(
        monthRecap("1234567", 3, 480)
    ));

    syncService.syncOfficeMonth(1L, TEST_YEAR_MONTH, Optional.empty(), new AtomicLong(0));

    assertThat(repo.count()).isEqualTo(0);
  }

  @Test
  void syncOfficeMonthWithNotBeforeFiltersOlderDays() {
    // 1 persona × 3 giorni (1, 2, 3 gennaio), notBefore=3 gennaio → solo il giorno 3
    when(epasClient.getMonthRecap(anyLong(), anyInt(), anyInt())).thenReturn(List.of(
        monthRecap("0001", 3, 480)
    ));

    syncService.syncOfficeMonth(
        1L, TEST_YEAR_MONTH,
        Optional.of(LocalDate.of(2026, 1, 3)),
        new AtomicLong(0));

    assertThat(repo.count()).isEqualTo(1);
  }
}
