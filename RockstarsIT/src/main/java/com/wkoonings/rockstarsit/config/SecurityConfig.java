package com.wkoonings.rockstarsit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/**").permitAll()
            .requestMatchers("/h2-console/**").permitAll()

            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/swagger-ui.html").permitAll()
            .requestMatchers("/swagger-ui/index.html").permitAll()
            .requestMatchers("/v3/api-docs/**").permitAll()
            .requestMatchers("/v3/api-docs").permitAll()
            .requestMatchers("/v3/api-docs.yaml").permitAll()
            .requestMatchers("/v3/api-docs/swagger-config").permitAll()
            .requestMatchers("/swagger-resources/**").permitAll()
            .requestMatchers("/swagger-resources").permitAll()
            .requestMatchers("/webjars/**").permitAll()
            .requestMatchers("/configuration/ui").permitAll()
            .requestMatchers("/configuration/security").permitAll()
            .requestMatchers("/swagger-config.json").permitAll()

            .anyRequest().authenticated()
        )
        .headers(headers -> headers
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

    return http.build();
  }
}
