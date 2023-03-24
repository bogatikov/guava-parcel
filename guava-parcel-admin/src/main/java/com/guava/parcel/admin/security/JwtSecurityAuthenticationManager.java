package com.guava.parcel.admin.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class JwtSecurityAuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTVerifier JWTVerifier = JWT.require(Algorithm.HMAC512("secret-here"))
            .withIssuer("guava-delivery-auth")
            .build();

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        try {
            var token = (String) authentication.getCredentials();
            var decoded = JWTVerifier.verify(token);
            var userId = decoded.getClaim("userId").asString();
            var email = decoded.getClaim("email").asString();
            var userType = decoded.getClaim("userType").asString();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userId,
                    token,
                    List.of(new SimpleGrantedAuthority(userType)));
            authenticationToken.setDetails(email);
            return Mono.just(authenticationToken);
        } catch (Exception exception) {
            log.error("Authentication failed", exception);
            return Mono.empty();
        }
    }
}