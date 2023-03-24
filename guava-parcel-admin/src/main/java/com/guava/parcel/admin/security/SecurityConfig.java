package com.guava.parcel.admin.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final ServerSecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity security) {
        return security
                .httpBasic().disable()
                .cors().disable()
                .formLogin().disable()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint((swe, ex) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .accessDeniedHandler((swe, ex) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                .and()
                .authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/sign-in").permitAll()
                .anyExchange().hasAuthority("ADMIN")
                .and()
                .build();
    }
}
