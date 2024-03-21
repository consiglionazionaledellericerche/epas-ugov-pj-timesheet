
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
 */package it.cnr.iit.epas.timesheet.ugovpj.service;

import java.util.concurrent.Semaphore;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Supporto per l'esecuzione di un'unica procedera per volta.
 *
 * @author Cristian Lucchesi
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SemaphoreService {

  private final Semaphore semaphore;

  public SemaphoreService() {
    semaphore = new Semaphore(1);
  }
  
  public boolean tryAcquire() {
    return semaphore.tryAcquire();
  }
  
  public void release() {
    semaphore.release();
  }

}