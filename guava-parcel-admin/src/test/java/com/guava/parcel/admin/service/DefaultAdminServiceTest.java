package com.guava.parcel.admin.service;

import com.guava.parcel.admin.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.admin.dto.form.CreateCourierForm;
import com.guava.parcel.admin.dto.form.SetCourierForm;
import com.guava.parcel.admin.dto.form.SignInForm;
import com.guava.parcel.admin.dto.view.CoordinateView;
import com.guava.parcel.admin.dto.view.CourierView;
import com.guava.parcel.admin.event.CourierCoordinateEvent;
import com.guava.parcel.admin.ext.AuthApi;
import com.guava.parcel.admin.ext.CourierApi;
import com.guava.parcel.admin.ext.ParcelDeliveryApi;
import com.guava.parcel.admin.ext.request.ChangeOrderStatusRequest;
import com.guava.parcel.admin.ext.request.CreateUserRequest;
import com.guava.parcel.admin.ext.request.SetCourierRequest;
import com.guava.parcel.admin.ext.request.SignInRequest;
import com.guava.parcel.admin.ext.response.CourierResponse;
import com.guava.parcel.admin.ext.response.OrderResponse;
import com.guava.parcel.admin.ext.response.OrderShortResponse;
import com.guava.parcel.admin.ext.response.SignInResponse;
import com.guava.parcel.admin.ext.response.UserResponse;
import com.guava.parcel.admin.model.Page;
import com.guava.parcel.admin.model.Status;
import com.guava.parcel.admin.model.UserType;
import com.guava.parcel.admin.stream.CourierCoordinateStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultAdminServiceTest {

    private CourierApi courierApi;
    private ParcelDeliveryApi parcelDeliveryApi;
    private AuthApi authApi;
    private CourierCoordinateStream coordinateStream;

    private static final ModelMapper mapper = new ModelMapper();
    private DefaultAdminService defaultAdminService;

    @BeforeEach
    void setUp() {
        courierApi = mock(CourierApi.class);
        parcelDeliveryApi = mock(ParcelDeliveryApi.class);
        authApi = mock(AuthApi.class);
        coordinateStream = mock(CourierCoordinateStream.class);
        defaultAdminService = new DefaultAdminService(
                courierApi,
                parcelDeliveryApi,
                authApi,
                mapper,
                coordinateStream
        );
    }

    @Test
    void signIn() {
        var email = "my@mail.com";
        var password = "secret";
        when(authApi.signIn(new SignInRequest(email, password)))
                .thenReturn(Mono.just(new SignInResponse("access-token-here", "refresh-token-here")));

        StepVerifier.create(defaultAdminService.signIn(new SignInForm(email, password)))
                .assertNext(signInView -> {
                    assertEquals("access-token-here", signInView.accessToken());
                    assertEquals("refresh-token-here", signInView.refreshToken());
                })
                .verifyComplete();
    }

    @Test
    void createCourier() {
        var email = "my@mail.com";
        when(authApi.createUser(new CreateUserRequest("Doe", "John", email, UserType.COURIER)))
                .thenReturn(Mono.just(new UserResponse("Doe", "John", email, UserType.COURIER)));

        StepVerifier.create(defaultAdminService.createCourier(new CreateCourierForm("Doe", "John", email)))
                .assertNext(userView -> {
                    assertEquals("Doe", userView.getLastName());
                    assertEquals("John", userView.getFirstName());
                    assertEquals(UserType.COURIER, userView.getUserType());
                    assertEquals(email, userView.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void changeOrderStatus() {
        UUID orderId = UUID.randomUUID();

        when(parcelDeliveryApi.changeOrderStatus(new ChangeOrderStatusRequest(orderId, Status.FINISHED)))
                .thenReturn(
                        Mono.just(
                                new OrderResponse(
                                        orderId,
                                        UUID.randomUUID(),
                                        UUID.randomUUID(),
                                        "The 1th Avenu",
                                        "The 2nd Avenue",
                                        Status.FINISHED,
                                        null,
                                        Instant.now())
                        )
                );

        StepVerifier.create(defaultAdminService.changeOrderStatus(new ChangeOrderStatusForm(orderId, Status.FINISHED)))
                .assertNext(orderView -> {
                    assertEquals(orderId, orderView.getId());
                    assertEquals(Status.FINISHED, orderView.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void getOrders() {
        when(parcelDeliveryApi.getOrders(Status.NEW, 0, 20))
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

        StepVerifier.create(defaultAdminService.getOrders(Status.NEW, 0, 20))
                .assertNext(page -> {
                    assertEquals(1, page.getTotalElements());
                    assertEquals(1, page.getNumberOfElements());
                    assertEquals(0, page.getCurrentPage());
                })
                .verifyComplete();
    }

    @Test
    void setCourier() {
        UUID orderId = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();
        when(parcelDeliveryApi.setCourier(new SetCourierRequest(orderId, courierId)))
                .thenReturn(
                        Mono.just(new OrderResponse(
                                orderId,
                                UUID.randomUUID(),
                                courierId,
                                "The 1th Avenue",
                                "The 2nd Avenue",
                                Status.NEW,
                                null,
                                Instant.now()
                        ))
                );

        StepVerifier.create(defaultAdminService.setCourier(new SetCourierForm(orderId, courierId)))
                .assertNext(orderView -> {
                    assertEquals(orderId, orderView.getId());
                    assertEquals(courierId, orderView.getCourierId());
                })
                .verifyComplete();
    }

    @Test
    void getCouriersEmptyPage() {
        when(courierApi.getCourierList(0, 20))
                .thenReturn(Mono.empty());

        StepVerifier.create(defaultAdminService.getCouriers(0, 20))
                .assertNext(page -> {
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(0, page.getTotalElements());
                    assertEquals(0, page.getNumberOfElements());
                    assertEquals(0, page.getContent().size());
                })
                .verifyComplete();
    }

    @Test
    void getCouriers() {
        when(courierApi.getCourierList(0, 20))
                .thenReturn(
                        Mono.just(
                                new Page<>(
                                        List.of(new CourierResponse(
                                                        "Doe",
                                                        "John",
                                                        Map.of(Status.NEW, 1, Status.FINISHED, 2)
                                                )
                                        ),
                                        0,
                                        1L,
                                        1
                                )
                        )
                );

        StepVerifier.create(defaultAdminService.getCouriers(0, 20))
                .assertNext(page -> {
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(1, page.getTotalElements());
                    assertEquals(1, page.getNumberOfElements());
                    assertEquals(1, page.getContent().size());
                    CourierView courierView = page.getContent().get(0);
                    assertEquals("Doe", courierView.getLastName());
                    assertEquals("John", courierView.getFirstName());
                    assertEquals(2, courierView.getOrderStats().size());
                    assertEquals(1, courierView.getOrderStats().get(Status.NEW));
                    assertEquals(2, courierView.getOrderStats().get(Status.FINISHED));

                    assertFalse(courierView.getOrderStats().containsKey(Status.CANCELED));
                    assertFalse(courierView.getOrderStats().containsKey(Status.WAITING_FOR_COURIER));
                    assertFalse(courierView.getOrderStats().containsKey(Status.DELIVERING));

                })
                .verifyComplete();
    }

    @Test
    void getOrder() {
        UUID orderId = UUID.randomUUID();
        when(parcelDeliveryApi.getOrder(orderId))
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

        StepVerifier.create(defaultAdminService.getOrder(orderId))
                .assertNext(orderView -> assertEquals(orderId, orderView.getId()))
                .verifyComplete();
    }

    @Test
    void subscribeCourierCoordinates() {
        UUID courierId1 = UUID.randomUUID();
        UUID courierId2 = UUID.randomUUID();

        when(coordinateStream.getStream()).thenReturn(
                Flux.just(
                        new CourierCoordinateEvent(courierId1, 1.0, 1.0),
                        new CourierCoordinateEvent(courierId1, 2.0, 2.0),
                        new CourierCoordinateEvent(courierId2, 3.0, 3.0)
                )
        );

        StepVerifier.create(defaultAdminService.subscribeCourierCoordinates(courierId1))
                .expectNext(new CoordinateView(courierId1, 1.0, 1.0))
                .expectNext(new CoordinateView(courierId1, 2.0, 2.0))
                .verifyComplete();
    }

    @Test
    void consumeCourierCoordinateEvent() {
        when(coordinateStream.publishToStream(any())).thenReturn(Mono.empty());

        StepVerifier.create(defaultAdminService.consumeCourierCoordinateEvent(any()))
                .verifyComplete();

        verify(coordinateStream, times(1)).publishToStream(any());
    }
}