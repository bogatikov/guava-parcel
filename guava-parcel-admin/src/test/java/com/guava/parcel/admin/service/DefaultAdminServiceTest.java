package com.guava.parcel.admin.service;

import com.guava.parcel.admin.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.admin.dto.form.CreateCourierForm;
import com.guava.parcel.admin.dto.form.SignInForm;
import com.guava.parcel.admin.ext.AuthApi;
import com.guava.parcel.admin.ext.ParcelDeliveryApi;
import com.guava.parcel.admin.ext.request.ChangeOrderStatusRequest;
import com.guava.parcel.admin.ext.request.CreateUserRequest;
import com.guava.parcel.admin.ext.request.SignInRequest;
import com.guava.parcel.admin.ext.response.OrderResponse;
import com.guava.parcel.admin.ext.response.SignInResponse;
import com.guava.parcel.admin.ext.response.UserResponse;
import com.guava.parcel.admin.model.Status;
import com.guava.parcel.admin.model.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultAdminServiceTest {

    private ParcelDeliveryApi parcelDeliveryApi;
    private AuthApi authApi;
    private static final ModelMapper mapper = new ModelMapper();
    private DefaultAdminService defaultAdminService;

    @BeforeEach
    void setUp() {
        parcelDeliveryApi = mock(ParcelDeliveryApi.class);
        authApi = mock(AuthApi.class);
        defaultAdminService = new DefaultAdminService(
                parcelDeliveryApi,
                authApi,
                mapper
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
        // TODO: 31.03.2023
    }

    @Test
    void setCourier() {
        // TODO: 31.03.2023
    }

    @Test
    void getCouriers() {
        // TODO: 31.03.2023
    }

    @Test
    void getOrder() {
        // TODO: 31.03.2023
    }

    @Test
    void subscribeCourierCoordinates() {
        // TODO: 31.03.2023
    }

    @Test
    void consumeCourierCoordinateEvent() {
        // TODO: 31.03.2023
    }
}