package com.guava.parcel.user.service;

import com.guava.parcel.user.dto.form.CancelOrderForm;
import com.guava.parcel.user.dto.form.ChangeDestinationForm;
import com.guava.parcel.user.dto.form.CreateOrderForm;
import com.guava.parcel.user.dto.form.SignInForm;
import com.guava.parcel.user.dto.form.SignUpForm;
import com.guava.parcel.user.dto.view.OrderShortView;
import com.guava.parcel.user.dto.view.OrderView;
import com.guava.parcel.user.dto.view.SignInView;
import com.guava.parcel.user.dto.view.SignUpView;
import com.guava.parcel.user.error.EntityNotFound;
import com.guava.parcel.user.ext.AuthApi;
import com.guava.parcel.user.ext.ParcelDeliveryApi;
import com.guava.parcel.user.ext.request.ChangeDestinationRequest;
import com.guava.parcel.user.ext.request.CreateOrderRequest;
import com.guava.parcel.user.ext.request.SignInRequest;
import com.guava.parcel.user.ext.request.SignUpRequest;
import com.guava.parcel.user.ext.response.OrderResponse;
import com.guava.parcel.user.model.Page;
import com.guava.parcel.user.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {
    private final AuthApi authApi;
    private final ParcelDeliveryApi deliveryApi;
    private final ModelMapper mapper;

    @Override
    public Mono<SignUpView> signUp(SignUpForm signUpForm) {
        return authApi.signUp(mapper.map(signUpForm, SignUpRequest.class))
                .map(signUpResponse -> mapper.map(signUpResponse, SignUpView.class));
    }

    @Override
    public Mono<SignInView> signIn(SignInForm signInForm) {
        return authApi.signIn(mapper.map(signInForm, SignInRequest.class))
                .map(signInResponse -> mapper.map(signInResponse, SignInView.class));
    }

    @Override
    public Mono<OrderView> createOrder(CreateOrderForm createOrderForm) {
        return resolveUserId()
                .flatMap(userId -> deliveryApi.createOrder(
                                new CreateOrderRequest(
                                        createOrderForm.sourceAddress(),
                                        createOrderForm.destinationAddress(),
                                        userId
                                )
                        )
                )
                .map(orderResponse -> mapper.map(orderResponse, OrderView.class));
    }

    @Override
    public Mono<OrderView> changeDestination(ChangeDestinationForm changeDestinationForm) {
        return getOrder(changeDestinationForm.orderId())
                .flatMap(order -> deliveryApi.changeDestination(new ChangeDestinationRequest(
                                        order.id(),
                                        changeDestinationForm.destinationAddress()
                                )
                        )
                )
                .map(orderResponse -> mapper.map(orderResponse, OrderView.class));
    }

    @Override
    public Mono<OrderView> cancelOrder(CancelOrderForm cancelOrderForm) {
        return null;
    }

    @Override
    public Mono<OrderView> getOrder(UUID orderId) {
        return resolveUserId().zipWith(deliveryApi.getOrder(orderId))
                .filter(tuple -> {
                    UUID userId = tuple.getT1();
                    OrderResponse orderResponse = tuple.getT2();
                    return orderResponse.userId().equals(userId);
                })
                .map(tuple -> mapper.map(tuple.getT2(), OrderView.class))
                .switchIfEmpty(Mono.error(new EntityNotFound("Order not found")));
    }

    @Override
    public Mono<Page<OrderShortView>> getOrders(Integer page, Integer size) {
        return null;
    }

    private Mono<UUID> resolveUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> UUID.fromString((String) authentication.getPrincipal()));
    }
}
