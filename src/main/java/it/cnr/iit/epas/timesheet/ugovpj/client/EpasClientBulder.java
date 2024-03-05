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
package it.cnr.iit.epas.timesheet.ugovpj.client;

import com.google.gson.GsonBuilder;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import it.cnr.iit.epas.timesheet.ugovpj.config.EpasConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builder per costruire un'istanza del client di OIL configurata.
 *
 * @author Cristian Lucchesi
 *
 */
@RequiredArgsConstructor
@Configuration
public class EpasClientBulder {

  private final EpasConfig config;
  private final GsonBuilder gsonBuilder;
  
  /**
   * Costruisce l'istance del client OIL con impostati i parametri 
   * di configurazione e le dipendenze necessario per l'autenticazione
   * e per l'invio delle immagini com multipart/form-data.
   */
  @Bean
  public EpasClient epasClient() {
    return Feign.builder()
        .requestInterceptor(
            new BasicAuthRequestInterceptor(
                config.getUsername(), 
                config.getPassword()))
        .encoder(new FormEncoder(new GsonEncoder(gsonBuilder.create())))
        .decoder(new GsonDecoder(gsonBuilder.create()))
        .target(EpasClient.class, config.getServerUrl());
  }
}