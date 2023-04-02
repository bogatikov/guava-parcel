package com.guava.parcel.courier.security;

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

    private static final String ACTUATOR_PATH_PATTERN = "/actuator/**";
    private static final String SIGN_IN_PATH_PATTERN = "/sign-in";
    private static final String COURIER_LIST_PATH_PATTERN = "/courier/list";
    private static final String COURIER_AUTHORITY = "COURIER";
    private static final String ADMIN_AUTHORITY = "ADMIN";
    private static final String SWAGGER_DOC = "/swagger-doc/**";


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
                .pathMatchers(SWAGGER_DOC).permitAll()
                .pathMatchers(ACTUATOR_PATH_PATTERN).permitAll()
                .pathMatchers(HttpMethod.POST, SIGN_IN_PATH_PATTERN).permitAll()
                .pathMatchers(COURIER_LIST_PATH_PATTERN).hasAuthority(ADMIN_AUTHORITY)
                .anyExchange().hasAuthority(COURIER_AUTHORITY)
                .and()
                .build();
    }
}
