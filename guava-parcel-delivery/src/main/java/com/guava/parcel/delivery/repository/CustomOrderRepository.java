package com.guava.parcel.delivery.repository;

import com.guava.parcel.delivery.model.Order;
import com.guava.parcel.delivery.model.Page;
import com.guava.parcel.delivery.model.filter.OrderFilter;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

public interface CustomOrderRepository {
    Mono<Page<Order>> getOrdersByFilter(OrderFilter orderFilter, Integer page, Integer size);

    Mono<Map<Order.Status, Integer>> getCourierStatsByCourierId(UUID courierId);
}
