package com.guava.parcel.courier.service;

import com.guava.parcel.courier.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.courier.dto.form.SignInForm;
import com.guava.parcel.courier.dto.view.OrderShortView;
import com.guava.parcel.courier.dto.view.OrderView;
import com.guava.parcel.courier.dto.view.SignInView;
import com.guava.parcel.courier.ext.AuthApi;
import com.guava.parcel.courier.ext.request.SignInRequest;
import com.guava.parcel.courier.model.Page;
import com.guava.parcel.courier.service.api.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultCourierService implements CourierService {
    private final AuthApi authApi;

    @Override
    public Mono<SignInView> signIn(SignInForm signInForm) {
        return authApi.signIn(new SignInRequest(signInForm.email(), signInForm.password()))
                .map(signInResponse -> new SignInView(signInResponse.accessToken(), signInResponse.refreshToken()));
    }

    @Override
    public Mono<Page<OrderShortView>> getOrders() {
        return null;
    }

    @Override
    public Mono<OrderView> changeStatus(ChangeOrderStatusForm changeStatusForm) {
        return null;
    }

    @Override
    public Mono<OrderView> getOrder(UUID orderId) {
        return null;
    }
}
