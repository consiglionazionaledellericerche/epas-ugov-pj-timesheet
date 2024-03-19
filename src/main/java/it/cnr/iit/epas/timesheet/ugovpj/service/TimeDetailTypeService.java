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

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import it.cnr.iit.epas.timesheet.ugovpj.config.CachingConfig;
import it.cnr.iit.epas.timesheet.ugovpj.model.TimeDetailType;
import it.cnr.iit.epas.timesheet.ugovpj.repo.TimeDetailTypeRepo;
import lombok.RequiredArgsConstructor;

/**
 * Contiene il metodo per prelevare i timeDetailTypes tramite
 * la cache.
 *
 * @author Cristian Lucchesi
 */
@RequiredArgsConstructor
@Service
public class TimeDetailTypeService {

  private final TimeDetailTypeRepo typeRepo;

  @Cacheable(CachingConfig.TIME_DETAIL_TYPES_CACHE_NAME)
  public Set<String> timeDetailTypes() {
    return typeRepo.findAll().stream().map(TimeDetailType::getCode).collect(Collectors.toSet());
  }

}