package com.guava.parcel.user.service.api;

import com.guava.parcel.user.dto.form.CancelOrderForm;
import com.guava.parcel.user.dto.form.ChangeDestinationForm;
import com.guava.parcel.user.dto.form.CreateOrderForm;
import com.guava.parcel.user.dto.form.SignInForm;
import com.guava.parcel.user.dto.form.SignUpForm;
import com.guava.parcel.user.dto.view.OrderShortView;
import com.guava.parcel.user.dto.view.OrderView;
import com.guava.parcel.user.dto.view.SignInView;
import com.guava.parcel.user.dto.view.SignUpView;
import com.guava.parcel.user.model.Page;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {
    Mono<SignUpView> signUp(SignUpForm signUpForm);
    Mono<SignInView> signIn(SignInForm signInForm);
    Mono<OrderView> createOrder(CreateOrderForm createOrderForm);
    Mono<OrderView> changeDestination(ChangeDestinationForm changeDestinationForm);
    Mono<OrderView> cancelOrder(CancelOrderForm cancelOrderForm);
    Mono<OrderView> getOrder(UUID orderId);
    Mono<Page<OrderShortView>> getOrders(Integer page, Integer size);

}
