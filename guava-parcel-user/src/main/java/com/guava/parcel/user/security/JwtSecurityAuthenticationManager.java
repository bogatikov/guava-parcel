package com.guava.parcel.user.security;

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

    private static final String USER_ID_CLAIM = "userId";
    private static final String EMAIL_CLAIM = "email";
    private static final String USER_TYPE_CLAIM = "userType";

    private final JWTVerifier JWTVerifier = JWT.require(Algorithm.HMAC512("secret"))
            .withIssuer("guava-parcel")
            .build();

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        try {
            var token = (String) authentication.getCredentials();
            var decoded = JWTVerifier.verify(token);
            var userId = decoded.getClaim(USER_ID_CLAIM).asString();
            var email = decoded.getClaim(EMAIL_CLAIM).asString();
            var userType = decoded.getClaim(USER_TYPE_CLAIM).asString();
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
