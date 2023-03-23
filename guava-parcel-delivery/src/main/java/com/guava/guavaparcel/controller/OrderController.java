package com.guava.guavaparcel.controller;

import com.guava.guavaparcel.dto.view.OrderShortView;
import com.guava.guavaparcel.dto.view.OrderView;
import com.guava.guavaparcel.model.Order;
import com.guava.guavaparcel.model.Page;
import com.guava.guavaparcel.service.api.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @GetMapping("list")
    //todo add validation
    public Mono<Page<OrderShortView>> getOrders(
            @RequestParam(value = "status", required = false) Order.Status status,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        return orderService.getOrders(status, page, size);
    }

    @GetMapping("{orderId}")
    public Mono<OrderView> getOrder(@Valid @NonNull @PathVariable UUID orderId) {
        return orderService.getOrder(orderId);
    }
}
