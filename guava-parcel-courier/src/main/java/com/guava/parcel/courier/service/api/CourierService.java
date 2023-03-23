package com.guava.parcel.courier.service.api;

import com.guava.parcel.courier.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.courier.dto.form.SignInForm;
import com.guava.parcel.courier.dto.view.OrderShortView;
import com.guava.parcel.courier.dto.view.OrderView;
import com.guava.parcel.courier.dto.view.SignInView;
import com.guava.parcel.courier.model.Page;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CourierService {
    Mono<SignInView> signIn(SignInForm signInForm);

    Mono<Page<OrderShortView>> getOrders();

    Mono<OrderView> changeStatus(ChangeOrderStatusForm changeStatusForm);

    Mono<OrderView> getOrder(UUID orderId);
}
