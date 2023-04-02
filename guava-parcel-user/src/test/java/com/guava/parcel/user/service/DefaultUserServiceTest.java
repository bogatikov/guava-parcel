package com.guava.parcel.user.service;

import com.guava.parcel.user.dto.form.CreateOrderForm;
import com.guava.parcel.user.dto.form.SignInForm;
import com.guava.parcel.user.ext.AuthApi;
import com.guava.parcel.user.ext.ParcelDeliveryApi;
import com.guava.parcel.user.ext.request.CreateOrderRequest;
import com.guava.parcel.user.ext.request.SignInRequest;
import com.guava.parcel.user.ext.response.OrderResponse;
import com.guava.parcel.user.ext.response.SignInResponse;
import com.guava.parcel.user.model.Status;
import com.guava.parcel.user.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultUserServiceTest {

    private UserService userService;
    private AuthApi authApi;
    private ParcelDeliveryApi deliveryApi;

    private static final ModelMapper mapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        authApi = mock(AuthApi.class);
        deliveryApi = mock(ParcelDeliveryApi.class);
        userService = new DefaultUserService(
                authApi,
                deliveryApi,
                mapper
        );
    }

    @Test
    void signUp() {
        // TODO: 03.04.2023
    }

    @Test
    void signIn() {
        var email = "my@mail.com";
        var password = "secret";
        when(authApi.signIn(new SignInRequest(email, password)))
                .thenReturn(Mono.just(new SignInResponse("access-token-here", "refresh-token-here")));

        StepVerifier.create(userService.signIn(new SignInForm(email, password)))
                .assertNext(signInView -> {
                    assertEquals("access-token-here", signInView.getAccessToken());
                    assertEquals("refresh-token-here", signInView.getRefreshToken());
                })
                .verifyComplete();
    }

    @Test
    void createOrder() {
        UUID userId = UUID.randomUUID();
        when(deliveryApi.createOrder(new CreateOrderRequest("The 1th Avenue", "The 2nd Avenue", userId)))
                .thenReturn(Mono.just(new OrderResponse(UUID.randomUUID(),
                        userId,
                        UUID.randomUUID(),
                        "The 1th Avenue",
                        "The 2nd Avenue",
                        Status.NEW,
                        null,
                        Instant.now())));

        StepVerifier.create(userService.createOrder(new CreateOrderForm("The 1th Avenue", "The 2nd Avenue"))
                        .contextWrite(mockAndGetSecurityContextWithPrincipal(userId)))
                .assertNext(orderView -> {
                    assertEquals(userId, orderView.getUserId());
                    assertEquals("The 2nd Avenue", orderView.getDestinationAddress());
                    assertEquals("The 1th Avenue", orderView.getSourceAddress());
                })
                .verifyComplete();
    }

    @Test
    void changeDestination() {
        // TODO: 03.04.2023  
    }

    @Test
    void cancelOrder() {
        // TODO: 03.04.2023  
    }

    @Test
    void getOrder() {
        // TODO: 03.04.2023  
    }

    @Test
    void getOrders() {
        // TODO: 03.04.2023  
    }


    private Context mockAndGetSecurityContextWithPrincipal(UUID any) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(any.toString());
        return ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext));
    }
}