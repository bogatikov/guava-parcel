package com.guava.parcel.courier.service;

import com.guava.parcel.courier.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.courier.dto.form.CoordinateForm;
import com.guava.parcel.courier.dto.form.SignInForm;
import com.guava.parcel.courier.dto.view.CourierView;
import com.guava.parcel.courier.error.EntityNotFound;
import com.guava.parcel.courier.event.CourierCoordinateEvent;
import com.guava.parcel.courier.ext.AuthApi;
import com.guava.parcel.courier.ext.ParcelDeliveryApi;
import com.guava.parcel.courier.ext.request.ChangeOrderStatusRequest;
import com.guava.parcel.courier.ext.request.SignInRequest;
import com.guava.parcel.courier.ext.response.CourierStatsResponse;
import com.guava.parcel.courier.ext.response.OrderResponse;
import com.guava.parcel.courier.ext.response.OrderShortResponse;
import com.guava.parcel.courier.ext.response.SignInResponse;
import com.guava.parcel.courier.ext.response.UserResponse;
import com.guava.parcel.courier.model.Page;
import com.guava.parcel.courier.model.Status;
import com.guava.parcel.courier.model.UserType;
import com.guava.parcel.courier.service.api.BroadcastService;
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
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
                                .contextWrite(mockAndGetSecurityContextWithPrincipal(courierId))
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
                                .contextWrite(mockAndGetSecurityContextWithPrincipal(courierId))
                )
                .verifyError(EntityNotFound.class);
    }

    @Test
    void changeStatus() {
        var orderId = UUID.randomUUID();
        var courierId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        Instant createdAt = Instant.now();

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
                                .contextWrite(mockAndGetSecurityContextWithPrincipal(courierId))
                )
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    void getOrderOfOtherCourier() {
        var orderId = UUID.randomUUID();
        var courierId = UUID.randomUUID();

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
                        defaultCourierService.getOrder(orderId)
                                .contextWrite(mockAndGetSecurityContextWithPrincipal(courierId))
                )
                .verifyError(EntityNotFound.class);
    }

    @Test
    void getOrder() {
        var orderId = UUID.randomUUID();
        var courierId = UUID.randomUUID();

        when(deliveryApi.getOrder(orderId)).thenReturn(
                Mono.just(
                        new OrderResponse(orderId,
                                UUID.randomUUID(),
                                courierId,
                                "Source",
                                "Dest",
                                Status.NEW,
                                null,
                                Instant.now()
                        )
                )
        );

        StepVerifier.create(
                        defaultCourierService.getOrder(orderId)
                                .contextWrite(mockAndGetSecurityContextWithPrincipal(courierId))
                )
                .assertNext(orderView -> {
                    assertEquals(orderId, orderView.getId());
                    assertEquals(courierId, orderView.getCourierId());
                })
                .verifyComplete();
    }

    @Test
    void sendCourierCoordinates() {
        UUID courierId = UUID.randomUUID();
        when(broadcastService.broadcastCourierCoordinateEvent(any())).thenReturn(Mono.empty());

        double longitude = 2.66;
        double latitude = 3.75;
        StepVerifier.create(
                        defaultCourierService
                                .sendCourierCoordinates(new CoordinateForm(longitude, latitude))
                                .contextWrite(mockAndGetSecurityContextWithPrincipal(courierId))
                )
                .assertNext(coordinateView -> {
                    assertEquals(longitude, coordinateView.longitude());
                    assertEquals(latitude, coordinateView.latitude());
                    assertEquals(courierId, coordinateView.courierId());
                })
                .verifyComplete();

        verify(broadcastService, times(1))
                .broadcastCourierCoordinateEvent(new CourierCoordinateEvent(courierId, longitude, latitude));
    }

    @Test
    void getCourierListEmptyPage() {
        when(authApi.getUserList(UserType.COURIER, 0, 20))
                .thenReturn(Mono.empty());
        StepVerifier.create(defaultCourierService.getCourierList(0, 20))
                .assertNext(page -> {
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(0, page.getTotalElements());
                    assertEquals(0, page.getNumberOfElements());
                    assertEquals(0, page.getContent().size());
                })
                .verifyComplete();
    }

    @Test
    void getCourierList() {
        UUID courierId = UUID.randomUUID();
        when(authApi.getUserList(UserType.COURIER, 0, 20))
                .thenReturn(
                        Mono.just(new Page<>(
                                List.of(new UserResponse(
                                        courierId,
                                        "Doe",
                                        "John",
                                        "john@doe.com",
                                        UserType.COURIER
                                )),
                                0,
                                1L,
                                1
                        ))
                );

        when(deliveryApi.getCourierStats(courierId))
                .thenReturn(Mono.just(new CourierStatsResponse(courierId, Map.of(Status.NEW, 3, Status.FINISHED, 4))));

        StepVerifier.create(defaultCourierService.getCourierList(0, 20))
                .assertNext(page -> {
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(1L, page.getTotalElements());
                    assertEquals(1, page.getNumberOfElements());
                    assertEquals(1, page.getContent().size());

                    CourierView courierView = page.getContent().get(0);
                    assertEquals("Doe", courierView.getLastName());
                    assertEquals("John", courierView.getFirstName());
                    assertEquals(2, courierView.getOrderStats().size());

                    assertTrue(courierView.getOrderStats().containsKey(Status.NEW));
                    assertTrue(courierView.getOrderStats().containsKey(Status.FINISHED));
                    assertEquals(3, courierView.getOrderStats().get(Status.NEW));
                    assertEquals(4, courierView.getOrderStats().get(Status.FINISHED));
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