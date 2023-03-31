package com.guava.parcel.courier.service;

import com.guava.parcel.courier.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.courier.dto.form.SignInForm;
import com.guava.parcel.courier.error.EntityNotFound;
import com.guava.parcel.courier.ext.AuthApi;
import com.guava.parcel.courier.ext.ParcelDeliveryApi;
import com.guava.parcel.courier.ext.request.ChangeOrderStatusRequest;
import com.guava.parcel.courier.ext.request.SignInRequest;
import com.guava.parcel.courier.ext.response.OrderResponse;
import com.guava.parcel.courier.ext.response.OrderShortResponse;
import com.guava.parcel.courier.ext.response.SignInResponse;
import com.guava.parcel.courier.model.Page;
import com.guava.parcel.courier.model.Status;
import com.guava.parcel.courier.service.api.BroadcastService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultCourierServiceTest {

    private DefaultCourierService defaultCourierService;
    private AuthApi authApi;
    private ParcelDeliveryApi deliveryApi;
    private BroadcastService broadcastService;

    private final static ModelMapper mapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        authApi = mock(AuthApi.class);
        deliveryApi = mock(ParcelDeliveryApi.class);
        broadcastService = mock(BroadcastService.class);
        defaultCourierService = new DefaultCourierService(
                authApi,
                deliveryApi,
                mapper,
                broadcastService
        );
    }

    @Test
    void signIn() {
        var email = "my@mail.com";
        var password = "secret";
        when(authApi.signIn(new SignInRequest(email, password)))
                .thenReturn(Mono.just(new SignInResponse("access-token-here", "refresh-token-here")));

        StepVerifier.create(defaultCourierService.signIn(new SignInForm(email, password)))
                .assertNext(signInView -> {
                    assertEquals("access-token-here", signInView.accessToken());
                    assertEquals("refresh-token-here", signInView.refreshToken());
                })
                .verifyComplete();
    }

    @Test
    void getOrders() {
        var courierId = UUID.randomUUID();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(courierId.toString());

        when(deliveryApi.getOrders(courierId, 0, 20))
                .thenReturn(
                        Mono.just(
                                new Page<>(
                                        List.of(new OrderShortResponse(UUID.randomUUID(), UUID.randomUUID(), Status.NEW, null, Instant.now())),
                                        0,
                                        1L,
                                        1
                                )
                        )
                );

        StepVerifier.create(
                        defaultCourierService.getOrders(0, 20)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                )
                .assertNext(page -> {
                    assertEquals(1, page.getTotalElements());
                    assertEquals(1, page.getNumberOfElements());
                    assertEquals(0, page.getCurrentPage());
                })
                .verifyComplete();
    }

    @Test
    void changeStatusWithOrderOfOtherCourier() {
        var orderId = UUID.randomUUID();
        var courierId = UUID.randomUUID();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(courierId.toString());

        when(deliveryApi.getOrder(orderId)).thenReturn(
                Mono.just(
                        new OrderResponse(orderId,
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "Source",
                                "Dest",
                                Status.NEW,
                                null,
                                Instant.now()
                        )
                )
        );

        StepVerifier.create(
                        defaultCourierService.changeStatus(new ChangeOrderStatusForm(orderId, Status.DELIVERING))
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                )
                .verifyError(EntityNotFound.class);

    }

    @Test
    void changeStatus() {
        var orderId = UUID.randomUUID();
        var courierId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(courierId.toString());

        OrderResponse orderResponse = new OrderResponse(
                orderId,
                userId,
                courierId,
                "Source",
                "Dest",
                Status.NEW,
                null,
                createdAt
        );

        when(deliveryApi.getOrder(orderId))
                .thenReturn(Mono.just(orderResponse));

        when(deliveryApi.changeOrderStatus(new ChangeOrderStatusRequest(orderId, Status.DELIVERING)))
                .thenReturn(
                        Mono.just(
                                new OrderResponse(
                                        orderId,
                                        userId,
                                        courierId,
                                        "Source",
                                        "Dest",
                                        Status.DELIVERING,
                                        null,
                                        createdAt
                                )
                        )
                );

        StepVerifier.create(
                        defaultCourierService
                                .changeStatus(new ChangeOrderStatusForm(orderId, Status.DELIVERING))
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                )
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    void getOrder() {
    }
}