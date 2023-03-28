package com.guava.parcel.courier.ext;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.guava.parcel.courier.BaseIT;
import com.guava.parcel.courier.ext.request.SignInRequest;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@WireMockTest(httpPort = 9999)
class AuthApiIT extends BaseIT {

    @Autowired
    private AuthApi authApi;

    @Test
    void signInSuccessful() {
        stubFor(post(urlPathEqualTo("/guava-auth/user/sign-in"))
                .withRequestBody(equalToJson("{\"email\": \"my@email.com\", \"password\":  \"secret\"}"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"accessToken\": \"access-token-here\", \"refreshToken\": \"refresh-token-here\"}")
                )
        );

        StepVerifier.create(authApi.signIn(new SignInRequest("my@email.com", "secret")))
                .assertNext(signInResponse -> {
                    assertEquals("access-token-here", signInResponse.accessToken());
                    assertEquals("refresh-token-here", signInResponse.refreshToken());
                })
                .verifyComplete();
    }

    @Test
    void signInError() {
        stubFor(post(urlPathEqualTo("/guava-auth/user/sign-in"))
                .withRequestBody(equalToJson("{\"email\": \"my@email.com\", \"password\":  \"secret\"}"))
                .willReturn(
                        aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"errorMessage\": \"User not found\"}")
                )
        );

        StepVerifier.create(authApi.signIn(new SignInRequest("my@email.com", "secret")))
                .expectErrorSatisfies(exception -> {
                    assertInstanceOf(FeignException.NotFound.class, exception);
                })
                .verify();
    }
}