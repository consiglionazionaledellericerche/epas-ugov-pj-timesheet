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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import it.cnr.iit.epas.timesheet.ugovpj.model.PersonTimeDetail;
import jakarta.transaction.Transactional;

/**
 * Repository per l'accesso e la gestione dei dati del PersonTimeDetail.
 *
 * @author Cristian Lucchesi
 */
public interface PersonTimeDetailRepo 
  extends JpaRepository<PersonTimeDetail,Long>, BaseJpaRepository<PersonTimeDetail, Long> {

  Page<PersonTimeDetail> findByNumber(String number, Pageable pageable);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM PersonTimeDetail ptd")
  void truncateTable();

  @Procedure(procedureName = "P_CARICA_MARCATURE")
  void loadDetails();

  @Procedure(procedureName = "P_CARICA_MARCATURE_JOB")
  void loadDetailsJob();

  @Transactional
  @Modifying
  @Query(value = "call IE_PJ.P_CARICA_MARCATURE()", nativeQuery = true)
  void loadDetailsNative();

  @Transactional
  @Modifying
  @Query(value = "call P_CARICA_MARCATURE_JOB", nativeQuery = true)
  void loadDetailsJobNative();

}