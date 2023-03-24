package com.guava.guavaparcel.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.guava.guavaparcel.auth.dto.form.RefreshTokenForm;
import com.guava.guavaparcel.auth.dto.view.TokenView;
import com.guava.guavaparcel.auth.model.User;
import com.guava.guavaparcel.auth.service.api.TokenService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Repository
public class DefaultTokenService implements TokenService {
    private static final String JWT_ISSUER = "guava-parcel";
    private static final String JWT_SECRET = "secret"; // TODO move to config
    private static final Duration JWT_LIVE_TIME = Duration.ofMinutes(60L); // TODO move to config
    private static final String USER_ID_CLAIM = "userId";
    private static final String EMAIL_CLAIM = "email";
    private static final String USER_TYPE_CLAIM = "userType";
    private static final int REFRESH_TOKEN_LENGTH = 128;

    @Override
    public Mono<TokenView> issueAccessToken(User user) {
        Algorithm algorithm = getAlgorithm();
        var token = JWT.create()
                .withIssuer(JWT_ISSUER)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(JWT_LIVE_TIME))
                .withClaim(USER_ID_CLAIM, user.getId().toString())
                .withClaim(EMAIL_CLAIM, user.getEmail())
                .withClaim(USER_TYPE_CLAIM, user.getUserType().name())
                .sign(algorithm);
        return Mono.just(new TokenView(token));
    }

    @Override
    public Mono<TokenView> issueRefreshToken(User user) {
        String token = RandomStringUtils.randomAlphabetic(REFRESH_TOKEN_LENGTH);
        //todo save refresh
        return Mono.just(new TokenView(token));
    }

    @Override
    public Mono<TokenView> refreshToken(RefreshTokenForm refreshTokenForm) {
        return null;
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC512(JWT_SECRET); // TODO replace with keys
    }
}
