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
package it.cnr.iit.epas.timesheet.ugovpj.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servizio per invocare le stored procedure Oracle.
 *
 * @author Cristian Lucchesi
 */
@Slf4j
@Service
public class PersonTimeDetailLoadService {

  private final String LOAD_DETAILS_PROCEDURE_NAME = "IE_PJ.TS_P_CARICA_MARCATURE";
  private final String LOAD_DETAILS_JOB_PROCEDURE_NAME = "IE_PJ.TS_P_CARICA_MARCATURE_JOB";

  @PersistenceContext
  private EntityManager entityManager;

  @Value("${spring.jpa.properties.hibernate.default_schema}")
  private String dbSchema;

  public void loadDetails() {
    log.info("Sto per effetuare la chiamata alla stored procedure {}", LOAD_DETAILS_PROCEDURE_NAME);
    entityManager.createStoredProcedureQuery(
        String.format("%s.%s", dbSchema, LOAD_DETAILS_PROCEDURE_NAME))
      .execute();
    log.info("Terminata la chiamata alla stored procedure {}", LOAD_DETAILS_PROCEDURE_NAME);
  }

  public void loadDetailsJob() {
    log.info("Sto per effetuare la chiamata alla stored procedure {}", LOAD_DETAILS_JOB_PROCEDURE_NAME);
    entityManager.createStoredProcedureQuery(
        String.format("%s.%s", dbSchema, LOAD_DETAILS_JOB_PROCEDURE_NAME))
      .execute();
    log.info("Terminata la chiamata alla stored procedure {}", LOAD_DETAILS_JOB_PROCEDURE_NAME);
  }

}