package com.guava.parcel.courier.controller;

import com.guava.parcel.courier.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.courier.dto.form.CoordinateForm;
import com.guava.parcel.courier.dto.form.SignInForm;
import com.guava.parcel.courier.dto.view.CoordinateView;
import com.guava.parcel.courier.dto.view.OrderShortView;
import com.guava.parcel.courier.dto.view.OrderView;
import com.guava.parcel.courier.dto.view.SignInView;
import com.guava.parcel.courier.model.Page;
import com.guava.parcel.courier.service.api.CourierService;
import lombok.RequiredArgsConstructor;
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
public class CourierRestController {
    private final CourierService courierService;

    @PostMapping("sign-in")
    public Mono<SignInView> signIn(@RequestBody @Valid SignInForm signInForm) {
        return courierService.signIn(signInForm);
    }

    @PostMapping("changeStatus")
    public Mono<OrderView> changeStatus(@RequestBody @Valid ChangeOrderStatusForm changeOrderStatusForm) {
        return courierService.changeStatus(changeOrderStatusForm);
    }

    @GetMapping("order/list")
    public Mono<Page<OrderShortView>> getOrders(
            @Valid @NotNull @RequestParam("page") Integer page,
            @Valid @NotNull @RequestParam("size") Integer size
    ) {
        return courierService.getOrders(page, size);
    }

    @GetMapping("order/{orderId}")
    public Mono<OrderView> getOrder(@Valid @NonNull @PathVariable UUID orderId) {
        return courierService.getOrder(orderId);
    }

    @PostMapping("courier/coordinate")
    public Mono<CoordinateView> sendCourierCoordinates(@RequestBody @Valid CoordinateForm coordinateForm) {
        return courierService.sendCourierCoordinates(coordinateForm);
    }
}
