package com.guava.guavaparcel.repository;

import com.guava.guavaparcel.BaseIT;
import com.guava.guavaparcel.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class OrderRepositoryIT extends BaseIT {

    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll().block();
    }

    @Test
    void findAllByUserIdShouldCompleteWhenThereIsNoOrders() {
        UUID userId = UUID.randomUUID();
        StepVerifier.create(orderRepository.findAllByUserId(userId, Pageable.unpaged()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void countByUserIdShouldReturnZeroWhenThereIsNoOrders() {
        UUID userId = UUID.randomUUID();
        StepVerifier.create(orderRepository.countByUserId(userId))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void countByUserIdShouldReturnOneWhenThereIsOneOrder() {
        UUID userId = UUID.randomUUID();

        var order = new Order(
                UUID.randomUUID(),
                "The 3d Avenue",
                "The 4th Avenue",
                userId,
                null,
                Order.Status.NEW,
                null,
                Instant.now(),
                1L,
                true
        );

        orderRepository.save(order).block();

        StepVerifier.create(orderRepository.countByUserId(userId))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void countByUserIdShouldReturnOneWhenThereIsManyOrdersWithDifferentUserId() {
        UUID userId = UUID.randomUUID();

        var currentUserOrder = new Order(
                UUID.randomUUID(),
                "The 3d Avenue",
                "The 4th Avenue",
                userId,
                null,
                Order.Status.NEW,
                null,
                Instant.now(),
                1L,
                true
        );

        var anotherUserOrder = new Order(
                UUID.randomUUID(),
                "The 3d Avenue",
                "The 4th Avenue",
                UUID.randomUUID(),
                null,
                Order.Status.NEW,
                null,
                Instant.now(),
                1L,
                true
        );

        orderRepository.saveAll(List.of(currentUserOrder, anotherUserOrder)).blockLast();

        StepVerifier.create(orderRepository.countByUserId(userId))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void countByStatusShouldReturnZeroWhenThereIsNoOrdersWithRequestedStatus() {
        StepVerifier.create(orderRepository.countByStatus(Order.Status.NEW))
                .expectNext(0L)
                .verifyComplete();
        StepVerifier.create(orderRepository.countByStatus(Order.Status.WAITING_FOR_COURIER))
                .expectNext(0L)
                .verifyComplete();
        StepVerifier.create(orderRepository.countByStatus(Order.Status.DELIVERING))
                .expectNext(0L)
                .verifyComplete();
        StepVerifier.create(orderRepository.countByStatus(Order.Status.CANCELED))
                .expectNext(0L)
                .verifyComplete();
        StepVerifier.create(orderRepository.countByStatus(Order.Status.FINISHED))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void countByStatusShouldReturnCountOfSavedOrders() {
        var newOrder = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.NEW, null, Instant.now(), 1L, true);

        var waitingOrder1 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.WAITING_FOR_COURIER, null, Instant.now(), 1L, true);
        var waitingOrder2 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.WAITING_FOR_COURIER, null, Instant.now(), 1L, true);

        var deliveringOrder1 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.DELIVERING, null, Instant.now(), 1L, true);
        var deliveringOrder2 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.DELIVERING, null, Instant.now(), 1L, true);
        var deliveringOrder3 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.DELIVERING, null, Instant.now(), 1L, true);

        var canceledOrder1 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.CANCELED, null, Instant.now(), 1L, true);
        var canceledOrder2 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.CANCELED, null, Instant.now(), 1L, true);
        var canceledOrder3 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.CANCELED, null, Instant.now(), 1L, true);
        var canceledOrder4 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.CANCELED, null, Instant.now(), 1L, true);

        var finishedOrder1 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.FINISHED, null, Instant.now(), 1L, true);
        var finishedOrder2 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.FINISHED, null, Instant.now(), 1L, true);
        var finishedOrder3 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.FINISHED, null, Instant.now(), 1L, true);
        var finishedOrder4 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.FINISHED, null, Instant.now(), 1L, true);
        var finishedOrder5 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.FINISHED, null, Instant.now(), 1L, true);

        orderRepository.saveAll(List.of(
                newOrder,
                waitingOrder1,
                waitingOrder2,
                deliveringOrder1,
                deliveringOrder2,
                deliveringOrder3,
                canceledOrder1,
                canceledOrder2,
                canceledOrder3,
                canceledOrder4,
                finishedOrder1,
                finishedOrder2,
                finishedOrder3,
                finishedOrder4,
                finishedOrder5
        )).blockLast();

        StepVerifier.create(orderRepository.countByStatus(Order.Status.NEW))
                .expectNext(1L)
                .verifyComplete();
        StepVerifier.create(orderRepository.countByStatus(Order.Status.WAITING_FOR_COURIER))
                .expectNext(2L)
                .verifyComplete();
        StepVerifier.create(orderRepository.countByStatus(Order.Status.DELIVERING))
                .expectNext(3L)
                .verifyComplete();
        StepVerifier.create(orderRepository.countByStatus(Order.Status.CANCELED))
                .expectNext(4L)
                .verifyComplete();
        StepVerifier.create(orderRepository.countByStatus(Order.Status.FINISHED))
                .expectNext(5L)
                .verifyComplete();
    }
}