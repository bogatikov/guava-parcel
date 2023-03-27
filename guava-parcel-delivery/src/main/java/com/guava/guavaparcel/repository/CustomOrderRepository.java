package com.guava.guavaparcel.repository;

import com.guava.guavaparcel.model.Order;
import com.guava.guavaparcel.model.Page;
import com.guava.guavaparcel.model.filter.OrderFilter;
import reactor.core.publisher.Mono;

public interface CustomOrderRepository {
    Mono<Page<Order>> getOrdersByFilter(OrderFilter orderFilter, Integer page, Integer size);

    OrderRepository getOriginal();
}
