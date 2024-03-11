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
package it.cnr.iit.epas.timesheet.ugovpj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

/**
 * Configurazione della catena di filtri necessaria per la security dell'applicazione.
 *
 * @author Cristian Lucchesi
 *
 */
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityFilter {

  private final SecurityConfig securityConfig;

  // User Creation
  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder encoder) {

    // InMemoryUserDetailsManager
    UserDetails admin = User.withUsername(securityConfig.getUsername())
        .password(encoder.encode(securityConfig.getPassword()))
        .build();
    return new InMemoryUserDetailsManager(admin);
  }

  /**
   * Configurazione della catena di filtri di autenticazione da applicare ai metodi REST.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
        .anyRequest().authenticated())
      .csrf(AbstractHttpConfigurer::disable)
      .httpBasic(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(8);
  }
}