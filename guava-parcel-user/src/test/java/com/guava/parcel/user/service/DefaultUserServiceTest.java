package com.guava.parcel.user.service;

import com.guava.parcel.user.dto.form.CancelOrderForm;
import com.guava.parcel.user.dto.form.ChangeDestinationForm;
import com.guava.parcel.user.dto.form.CreateOrderForm;
import com.guava.parcel.user.dto.form.SignInForm;
import com.guava.parcel.user.dto.view.OrderShortView;
import com.guava.parcel.user.error.EntityNotFound;
import com.guava.parcel.user.ext.AuthApi;
import com.guava.parcel.user.ext.ParcelDeliveryApi;
import com.guava.parcel.user.ext.request.ChangeDestinationRequest;
import com.guava.parcel.user.ext.request.ChangeOrderStatusRequest;
import com.guava.parcel.user.ext.request.CreateOrderRequest;
import com.guava.parcel.user.ext.request.SignInRequest;
import com.guava.parcel.user.ext.response.OrderResponse;
import com.guava.parcel.user.ext.response.OrderShortResponse;
import com.guava.parcel.user.ext.response.SignInResponse;
import com.guava.parcel.user.model.Page;
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
import java.util.List;
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
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(deliveryApi.getOrder(orderId))
                .thenReturn(Mono.just(new OrderResponse(
                                orderId,
                                userId,
                                UUID.randomUUID(),
                                "The 1th Avenue",
                                "The 2nd Avenue",
                                Status.NEW,
                                null,
                                Instant.now()
                        ))
                );

        when(deliveryApi.changeDestination(new ChangeDestinationRequest(orderId, "Baker st.")))
                .thenReturn(Mono.just(new OrderResponse(
                                orderId,
                                userId,
                                UUID.randomUUID(),
                                "The 1th Avenue",
                                "Baker st.",
                                Status.NEW,
                                null,
                                Instant.now())
                        )
                );

        StepVerifier.create(
                        userService.changeDestination(new ChangeDestinationForm(orderId, "Baker st."))
                                .contextWrite(mockAndGetSecurityContextWithPrincipal(userId))
                )
                .assertNext(orderView -> {
                    assertEquals(userId, orderView.getUserId());
                    assertEquals("Baker st.", orderView.getDestinationAddress());
                    assertEquals("The 1th Avenue", orderView.getSourceAddress());
                })
                .verifyComplete();
    }

    @Test
    void changeDestinationOrderOfOtherUser() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(deliveryApi.getOrder(orderId))
                .thenReturn(Mono.just(new OrderResponse(
                                orderId,
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "The 1th Avenue",
                                "The 2nd Avenue",
                                Status.NEW,
                                null,
                                Instant.now()
                        ))
                );

        when(deliveryApi.changeDestination(new ChangeDestinationRequest(orderId, "Baker st.")))
                .thenReturn(Mono.just(new OrderResponse(
                                orderId,
                                userId,
                                UUID.randomUUID(),
                                "The 1th Avenue",
                                "Baker st.",
                                Status.NEW,
                                null,
                                Instant.now())
                        )
                );

        StepVerifier.create(
                        userService.changeDestination(new ChangeDestinationForm(orderId, "Baker st."))
                                .contextWrite(mockAndGetSecurityContextWithPrincipal(userId))
                )
                .verifyError(EntityNotFound.class);
    }

    @Test
    void cancelOrder() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(deliveryApi.getOrder(orderId))
                .thenReturn(Mono.just(new OrderResponse(
                                orderId,
                                userId,
                                UUID.randomUUID(),
                                "The 1th Avenue",
                                "The 2nd Avenue",
                                Status.NEW,
                                null,
                                Instant.now()
                        ))
                );

        when(deliveryApi.changeOrderStatus(new ChangeOrderStatusRequest(orderId, Status.CANCELED)))
                .thenReturn(Mono.just(new OrderResponse(
                                orderId,
                                userId,
                                UUID.randomUUID(),
                                "The 1th Avenue",
                                "Baker st.",
                                Status.CANCELED,
                                null,
                                Instant.now())
                        )
                );

        StepVerifier.create(
                        userService.cancelOrder(new CancelOrderForm(orderId))
                                .contextWrite(mockAndGetSecurityContextWithPrincipal(userId))
                )
                .assertNext(orderView -> {
                    assertEquals(userId, orderView.getUserId());
                    assertEquals(Status.CANCELED, orderView.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void cancelOrderOfOtherUser() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(deliveryApi.getOrder(orderId))
                .thenReturn(Mono.just(new OrderResponse(
                                orderId,
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "The 1th Avenue",
                                "The 2nd Avenue",
                                Status.NEW,
                                null,
                                Instant.now()
                        ))
                );

        when(deliveryApi.changeOrderStatus(new ChangeOrderStatusRequest(orderId, Status.CANCELED)))
                .thenReturn(Mono.just(new OrderResponse(
                                orderId,
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "The 1th Avenue",
                                "Baker st.",
                                Status.CANCELED,
                                null,
                                Instant.now())
                        )
                );

        StepVerifier.create(
                        userService.cancelOrder(new CancelOrderForm(orderId))
                                .contextWrite(mockAndGetSecurityContextWithPrincipal(userId))
                )
                .verifyError(EntityNotFound.class);
    }

    @Test
    void getOrder() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(deliveryApi.getOrder(orderId))
                .thenReturn(
                        Mono.just(new OrderResponse(
                                orderId,
                                userId,
                                UUID.randomUUID(),
                                "The 1th Avenue",
                                "The 2nd Avenue",
                                Status.NEW,
                                null,
                                Instant.now()
                        ))
                );

        StepVerifier.create(userService.getOrder(orderId)
                        .contextWrite(mockAndGetSecurityContextWithPrincipal(userId)))
                .assertNext(orderView -> assertEquals(orderId, orderView.getId()))
                .verifyComplete();
    }

    @Test
    void getOrderOfOtherUser() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(deliveryApi.getOrder(orderId))
                .thenReturn(
                        Mono.just(new OrderResponse(
                                orderId,
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "The 1th Avenue",
                                "The 2nd Avenue",
                                Status.NEW,
                                null,
                                Instant.now()
                        ))
                );

        StepVerifier.create(userService.getOrder(orderId)
                        .contextWrite(mockAndGetSecurityContextWithPrincipal(userId)))
                .verifyError(EntityNotFound.class);
    }

    @Test
    void getOrders() {
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        when(deliveryApi.getOrders(userId, 0, 20))
                .thenReturn(
                        Mono.just(
                                new Page<>(
                                        List.of(new OrderShortResponse(orderId, userId, Status.NEW, null, Instant.now())),
                                        0,
                                        1L,
                                        1
                                )
                        )
                );

        StepVerifier.create(userService.getOrders(0, 20)
                        .contextWrite(mockAndGetSecurityContextWithPrincipal(userId)))
                .assertNext(page -> {
                    assertEquals(1, page.getTotalElements());
                    assertEquals(1, page.getNumberOfElements());
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(1, page.getContent().size());
                    OrderShortView orderShortView = page.getContent().get(0);
                    assertEquals(orderId, orderShortView.getId());
                    assertEquals(userId, orderShortView.getUserId());
                })
                .verifyComplete();
    }


    private Context mockAndGetSecurityContextWithPrincipal(UUID any) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(any.toString());
        return ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext));
    }
}