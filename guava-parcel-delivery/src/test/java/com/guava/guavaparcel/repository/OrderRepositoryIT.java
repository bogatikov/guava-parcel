package com.guava.guavaparcel.repository;

import com.guava.guavaparcel.BaseIT;
import com.guava.guavaparcel.model.Order;
import com.guava.guavaparcel.model.filter.OrderFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OrderRepositoryIT extends BaseIT {

    class TestOrders {
        static final Order newOrder1 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.NEW, null, Instant.now(), 1L, true);
        static final Order newOrder2 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.NEW, null, Instant.now(), 1L, true);

        static final Order waitingOrder1 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.WAITING_FOR_COURIER, null, Instant.now(), 1L, true);
        static final Order waitingOrder2 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.WAITING_FOR_COURIER, null, Instant.now(), 1L, true);

        static final Order deliveringOrder1 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.DELIVERING, null, Instant.now(), 1L, true);
        static final Order deliveringOrder2 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.DELIVERING, null, Instant.now(), 1L, true);
        static final Order deliveringOrder3 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.DELIVERING, null, Instant.now(), 1L, true);

        static final Order canceledOrder1 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.CANCELED, null, Instant.now(), 1L, true);
        static final Order canceledOrder2 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.CANCELED, null, Instant.now(), 1L, true);
        static final Order canceledOrder3 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.CANCELED, null, Instant.now(), 1L, true);
        static final Order canceledOrder4 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.CANCELED, null, Instant.now(), 1L, true);

        static final Order finishedOrder1 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.FINISHED, null, Instant.now(), 1L, true);
        static final Order finishedOrder2 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.FINISHED, null, Instant.now(), 1L, true);
        static final Order finishedOrder3 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.FINISHED, null, Instant.now(), 1L, true);
        static final Order finishedOrder4 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.FINISHED, null, Instant.now(), 1L, true);
        static final Order finishedOrder5 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.FINISHED, null, Instant.now(), 1L, true);
    }

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CustomOrderRepository customOrderRepository;

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
    public void orderFilterShouldReturnEmptyPage() {
        OrderFilter orderFilter = new OrderFilter(null, null, Order.Status.NEW);

        StepVerifier.create(customOrderRepository.getOrdersByFilter(orderFilter, 0, 20))
                .assertNext(page -> {
                    assertEquals(0, page.getContent().size());
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(0, page.getNumberOfElements());
                    assertEquals(0, page.getTotalElements());
                })
                .verifyComplete();
    }

    @Test
    void getOrdersByStatusShouldReturnPage() {
        var content = List.of(TestOrders.newOrder1, TestOrders.newOrder2, TestOrders.finishedOrder1);
        orderRepository.saveAll(content).blockLast();

        StepVerifier.create(customOrderRepository.getOrdersByFilter(
                                new OrderFilter(null, null, Order.Status.NEW),
                                0,
                                20
                        )
                )
                .assertNext(page -> {
                    assertAll(page.getContent().stream().map(order -> () -> assertEquals(Order.Status.NEW, order.getStatus())));
                    assertEquals(2, page.getContent().size());
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(2, page.getNumberOfElements());
                    assertEquals(2, page.getTotalElements());
                })
                .verifyComplete();
    }

    @Test
    void getOrdersByStatusShouldReturnPageWithPartOfOrders() {
        var content = List.of(
                TestOrders.finishedOrder1,
                TestOrders.finishedOrder2,
                TestOrders.finishedOrder3,
                TestOrders.finishedOrder4,
                TestOrders.finishedOrder5
        );

        orderRepository.saveAll(content).blockLast();

        StepVerifier.create(customOrderRepository.getOrdersByFilter(
                                new OrderFilter(null, null, Order.Status.FINISHED),
                                0,
                                3
                        )
                )
                .assertNext(page -> {
                    assertAll(page.getContent().stream().map(order -> () -> assertEquals(Order.Status.FINISHED, order.getStatus())));
                    assertEquals(3, page.getContent().size());
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(3, page.getNumberOfElements());
                    assertEquals(5, page.getTotalElements());
                })
                .verifyComplete();
    }
}