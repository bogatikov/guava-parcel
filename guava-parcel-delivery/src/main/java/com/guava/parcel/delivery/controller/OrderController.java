package com.guava.parcel.delivery.controller;

import com.guava.parcel.delivery.dto.form.ChangeDestinationForm;
import com.guava.parcel.delivery.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.delivery.dto.form.CreateOrderForm;
import com.guava.parcel.delivery.dto.form.SetCourierForm;
import com.guava.parcel.delivery.dto.view.OrderShortView;
import com.guava.parcel.delivery.dto.view.OrderView;
import com.guava.parcel.delivery.model.Order;
import com.guava.parcel.delivery.model.Page;
import com.guava.parcel.delivery.model.filter.OrderFilter;
import com.guava.parcel.delivery.service.api.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @GetMapping("list")
    public Mono<Page<OrderShortView>> getOrders(
            @RequestParam(name = "userId", required = false) UUID userId,
            @RequestParam(name = "courierId", required = false) UUID courierId,
            @RequestParam(name = "status", required = false) Order.Status status,
            @Valid @NotNull @RequestParam("page") Integer page,
            @Valid @NotNull @RequestParam("size") Integer size
    ) {
        return orderService.getOrders(new OrderFilter(userId, courierId, status), page, size);
    }

    @GetMapping("{orderId}")
    public Mono<OrderView> getOrder(@Valid @NotNull @PathVariable UUID orderId) {
        return orderService.getOrder(orderId);
    }

    @PostMapping("changeStatus")
    public Mono<OrderView> changeOrderStatus(ChangeOrderStatusForm changeOrderStatusForm) {
        return orderService.changeOrderStatus(changeOrderStatusForm);
    }

    @PostMapping("setCourier")
    public Mono<OrderView> setCourier(@RequestBody @Valid SetCourierForm setCourierForm) {
        return orderService.setCourier(setCourierForm);
    }

    @PostMapping("create")
    // todo test
    public Mono<OrderView> createOrder(@RequestBody @Valid CreateOrderForm createOrderForm) {
        return orderService.createOrder(createOrderForm);
    }

    @PostMapping("changeDestination")
    // todo test
    public Mono<OrderView> changeDestination(@RequestBody @Valid ChangeDestinationForm changeDestinationForm) {
        return orderService.changeDestination(changeDestinationForm);
    }
}
