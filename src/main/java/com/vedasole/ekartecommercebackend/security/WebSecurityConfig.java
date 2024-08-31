package com.vedasole.ekartecommercebackend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JWTAuthenticationFilter jwtAuthFilter;
    private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AuthenticationProvider authenticationProvider;
    private static final String WEBHOOK_URL = "/api/v1/payment/webhook/*";

    private static final String[] PUBLIC_URLS = {
            "/api/v1/",
            "/api/v1/auth/*",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/api/v2/**",
            "/v2/api-docs/**",
            "/h2-console/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
                 .csrf()
                 .disable()
                 .authorizeHttpRequests(auth -> auth
                                 .anyRequest()
                                 .permitAll()
                 )
                 .exceptionHandling( exceptionHandling ->
                         exceptionHandling
                                 .authenticationEntryPoint(this.jwtAuthenticationEntryPoint)
                 )
                 .sessionManagement( sessionManagement ->
                         sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                 )
                 .authenticationProvider(authenticationProvider)
                 .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                 .formLogin().disable()
                 .cors()
                 .and()
                 .headers().frameOptions().disable();

        return http.build();
    }
}