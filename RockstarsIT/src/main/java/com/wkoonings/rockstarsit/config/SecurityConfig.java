package com.wkoonings.rockstarsit.config;

import com.wkoonings.rockstarsit.auth.BearerTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final BearerTokenFilter bearerTokenFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/auth/login").permitAll()
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

            // Protected endpoints - require authentication
            .requestMatchers("/api/**").authenticated()
            .anyRequest().authenticated()
        )
        .headers(headers -> headers
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        .addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
