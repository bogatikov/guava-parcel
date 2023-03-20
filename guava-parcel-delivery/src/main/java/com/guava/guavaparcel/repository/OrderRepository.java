package com.guava.guavaparcel.repository;

import com.guava.guavaparcel.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, UUID> {
    Flux<Order> findAllByUserId(UUID userId, Pageable pageable);

    Mono<Long> countByUserId(UUID userId);

    Flux<Order> findAllByStatus(Order.Status status, Pageable pageable);

    Mono<Long> countByStatus(Order.Status status);
}
