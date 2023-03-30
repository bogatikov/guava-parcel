package com.guava.parcel.delivery.repository;

import com.guava.parcel.delivery.model.Order;
import com.guava.parcel.delivery.model.Page;
import com.guava.parcel.delivery.model.filter.OrderFilter;
import reactor.core.publisher.Mono;

public interface CustomOrderRepository {
    Mono<Page<Order>> getOrdersByFilter(OrderFilter orderFilter, Integer page, Integer size);
}
