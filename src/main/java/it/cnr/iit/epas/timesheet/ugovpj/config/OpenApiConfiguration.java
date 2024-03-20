/*
 * Copyright (C) 2024 Consiglio Nazionale delle Ricerche
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
package it.cnr.iit.epas.timesheet.ugovpj.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione dei parametri generali della documentazione tramite OpenAPI.
 *
 * @author Cristian Lucchesi
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(title = "ePAS UGov PJ Timesheet",
    version = "0.1.0", 
    description = "ePAS UGov PJ Timesheet è il servizio per l'integrazione dei dati delle presenze/assenze presenti"
        + "in ePAS con le tabelle di frontiera Oracle di UGov PJ UWeb Timesheet Intime di Cineca"),
    servers = {
        @Server(url = "/", description = "ePAS UGov PJ Timesheet URL")}
    )
@SecuritySchemes(value = {
    @SecurityScheme(
        name = OpenApiConfiguration.BASIC_AUTHENTICATION,
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        scheme = "basic")
})
public class OpenApiConfiguration {

  public static final String BASIC_AUTHENTICATION = "Basic Authentication";

}