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

import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Contiene i metodi di supporto per ripulire le cache.
 *
 * @author Cristian Lucchesi
 */
@RequiredArgsConstructor
@Service
public class CachingService {

  private final CacheManager cacheManager;

  public void evictAllCaches() {
    cacheManager.getCacheNames().stream()
      .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
  }

  @Scheduled(fixedRateString = "${caching.spring.timeDetailTypes}")
  public void evictAllcachesAtIntervals() {
      evictAllCaches();
  }

}