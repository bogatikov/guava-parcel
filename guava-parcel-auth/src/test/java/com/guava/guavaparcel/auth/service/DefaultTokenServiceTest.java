package com.guava.guavaparcel.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.guava.guavaparcel.auth.model.User;
import com.guava.guavaparcel.auth.service.api.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultTokenServiceTest {

    private final TokenService tokenService = new DefaultTokenService();

    @BeforeEach
    void setUp() {
    }

    @Test
    public void tokenShouldContainUserDetails() {
        String email = "my@email.ru";
        UUID id = UUID.randomUUID();
        var user = new User(
                id,
                email,
                "John",
                "Doe",
                "password",
                User.UserType.USER,
                Instant.now(),
                Instant.now(),
                1L,
                false
        );

        var token = tokenService.issueAccessToken(user).block();
        Assertions.assertNotNull(token);
        DecodedJWT decode = JWT.decode(token.token());
        assertEquals(email, decode.getClaim("email").asString());
        assertEquals("USER", decode.getClaim("userType").asString());
        assertEquals(id.toString(), decode.getClaim("userId").asString());
    }
}