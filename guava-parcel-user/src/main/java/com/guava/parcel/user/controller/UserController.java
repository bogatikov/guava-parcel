package com.guava.parcel.user.controller;

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
import com.guava.parcel.user.service.api.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("order/list")
    public Mono<Page<OrderShortView>> getOrders(
            @Valid @NotNull @RequestParam("page") Integer page,
            @Valid @NotNull @RequestParam("size") Integer size
    ) {
        return userService.getOrders(page, size);
    }

    @GetMapping("order/{orderId}")
    public Mono<OrderView> getOrder(@Valid @NonNull @PathVariable UUID orderId) {
        return userService.getOrder(orderId);
    }

    @PostMapping("sign-in")
    public Mono<SignInView> signIn(@Valid @RequestBody SignInForm signInForm) {
        return userService.signIn(signInForm);
    }

    @PostMapping("sign-up")
    public Mono<SignUpView> signUp(@Valid @RequestBody SignUpForm signUpForm) {
        return userService.signUp(signUpForm);
    }

    @PostMapping("order/create")
    public Mono<OrderView> createOrder(@Valid @RequestBody CreateOrderForm createOrderForm) {
        return userService.createOrder(createOrderForm);
    }

    @PostMapping("order/changeDestination")
    public Mono<OrderView> createOrder(@Valid @RequestBody ChangeDestinationForm changeDestinationForm) {
        return userService.changeDestination(changeDestinationForm);
    }

    @PostMapping("order/cancel")
    public Mono<OrderView> cancelOrder(@Valid @RequestBody CancelOrderForm cancelOrderForm) {
        return userService.cancelOrder(cancelOrderForm);
    }
}
