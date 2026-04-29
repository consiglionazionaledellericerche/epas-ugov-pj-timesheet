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

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class AbsenceShowTerseDtoTest {

    @Test
    public void testGetJustifiedTimeFormatted() {
        AbsenceShowTerseDto dto = AbsenceShowTerseDto.builder()
                .justifiedTime(75)
                .build();
        assertThat(dto.getJustifiedTimeFormatted()).isEqualTo("01:15");

        dto.setJustifiedTime(10);
        assertThat(dto.getJustifiedTimeFormatted()).isEqualTo("00:10");

        dto.setJustifiedTime(125);
        assertThat(dto.getJustifiedTimeFormatted()).isEqualTo("02:05");

        dto.setJustifiedTime(0);
        assertThat(dto.getJustifiedTimeFormatted()).isEqualTo("00:00");

        dto.setJustifiedTime(null);
        assertThat(dto.getJustifiedTimeFormatted()).isEqualTo("00:00");
        
        dto.setJustifiedTime(600);
        assertThat(dto.getJustifiedTimeFormatted()).isEqualTo("10:00");
    }
}
