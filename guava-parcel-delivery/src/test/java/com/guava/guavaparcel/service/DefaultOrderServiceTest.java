package com.guava.guavaparcel.service;

import com.guava.guavaparcel.dto.form.CancelOrderForm;
import com.guava.guavaparcel.dto.form.ChangeDestinationForm;
import com.guava.guavaparcel.dto.form.CreateOrderForm;
import com.guava.guavaparcel.dto.view.OrderView;
import com.guava.guavaparcel.error.EntityNotFound;
import com.guava.guavaparcel.model.Order;
import com.guava.guavaparcel.repository.OrderRepository;
import com.guava.guavaparcel.service.api.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultOrderServiceTest {

    private OrderService orderService;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderService = new DefaultOrderService(orderRepository);
    }

    @Test
    void createOrder() {
        when(orderRepository.save(any())).thenAnswer(answer -> Mono.just(answer.getArgument(0)));

        UUID userId = UUID.randomUUID();
        StepVerifier.create(orderService.createOrder(new CreateOrderForm("The 1st Avenue",
                        "The 2nd Avenue",
                        userId)))
                .assertNext(orderView -> {
                    assertNotNull(orderView);
                    assertEquals("The 1st Avenue", orderView.sourceAddress());
                    assertEquals("The 2nd Avenue", orderView.destinationAddress());
                    assertEquals(Order.Status.NEW, orderView.status());
                    assertNull(orderView.courierId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("When trying to cancel order that doesn't exists expect raising EntityNotFoundException")
    void cancelOrderShouldThrowEntityNotFoundWhenOrderDoesntExists() {
        var orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Mono.empty());
        StepVerifier.create(orderService.cancelOrder(new CancelOrderForm(orderId)))
                .verifyError(EntityNotFound.class);
    }

    @Test
    void cancelOrderShouldThrowEntityNofFoundIfOrderAlreadyFinished() {
        var orderId = UUID.randomUUID();

        var order = new Order(
                orderId,
                "source address",
                "destination address",
                UUID.randomUUID(),
                UUID.randomUUID(),
                Order.Status.FINISHED,
                null,
                Instant.now(),
                1L,
                true
        );

        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));
        when(orderRepository.save(any())).thenAnswer(answer -> Mono.just(answer.getArgument(0)));
        StepVerifier.create(orderService.cancelOrder(new CancelOrderForm(orderId)))
                .verifyError(EntityNotFound.class);
    }

    @Test
    void cancelOrderShouldSaveOrderWithNewStatus() {
        var orderId = UUID.randomUUID();

        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        var order = new Order(
                orderId,
                "The 1t Avenue",
                "The 2nd Avenue",
                userId,
                null,
                Order.Status.NEW,
                null,
                createdAt,
                1L,
                true
        );

        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));
        when(orderRepository.save(any())).thenAnswer(answer -> Mono.just(answer.getArgument(0)));
        StepVerifier.create(orderService.cancelOrder(new CancelOrderForm(orderId)))
                .expectNext(new OrderView(
                                orderId,
                                userId,
                                null,
                                "The 1t Avenue",
                                "The 2nd Avenue",
                                Order.Status.CANCELED,
                                null,
                                createdAt
                        )
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("When trying to change destination for order that doesn't exists expect raising EntityNotFoundException")
    void changeDestinationShouldThrowEntityNotFoundWhenOrderDoesntExists() {
        var orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Mono.empty());
        StepVerifier.create(orderService.changeDestination(new ChangeDestinationForm(orderId, "new address")))
                .verifyError(EntityNotFound.class);
    }

    @Test
    void getOrders() {
    }

    @Test
    void getOrderThrowExceptionWhenOrderDoesntExists() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Mono.empty());

        StepVerifier.create(orderService.getOrder(orderId))
                .verifyError(EntityNotFound.class);
    }

    @Test
    void getOrderThrowExceptionWhenOrderExists() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        var order = new Order(
                orderId,
                "The 3d Avenue",
                "The 5th Avenue",
                userId,
                null,
                Order.Status.NEW,
                null,
                createdAt,
                1L,
                false
        );

        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));

        StepVerifier.create(orderService.getOrder(orderId))
                .expectNext(new OrderView(
                                orderId,
                                userId,
                                null,
                                "The 3d Avenue",
                                "The 5th Avenue",
                                Order.Status.NEW,
                                null,
                                createdAt
                        )
                )
                .verifyComplete();
    }

    @Test
    void cancelOrder() {
    }

    @Test
    void changeDestination() {
    }

    @Test
    void getOrdersByStatusShouldReturnEmptyPage() {
        when(orderRepository.findAllByStatus(Order.Status.NEW, PageRequest.of(0, 20)))
                .thenReturn(Flux.empty());
        when(orderRepository.countByStatus(Order.Status.NEW)).thenReturn(Mono.just(0L));

        StepVerifier.create(orderService.getOrders(Order.Status.NEW, 0, 20))
                .assertNext(page -> {
                    assertEquals(0, page.getContent().size());
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(0, page.getNumberOfElements());
                    assertEquals(0, page.getTotalElements());
                })
                .verifyComplete();
    }

    @Test
    void getOrdersByStatusShouldReturnNewPage() {
        var newOrder1 = new Order(UUID.randomUUID(), "The 1d Avenue", "The 2d Avenue", UUID.randomUUID(), null, Order.Status.NEW, null, Instant.now(), 1L, true);
        var newOrder2 = new Order(UUID.randomUUID(), "The 3d Avenue", "The 4th Avenue", UUID.randomUUID(), null, Order.Status.NEW, null, Instant.now(), 1L, true);

        var content = List.of(newOrder1, newOrder2);
        when(orderRepository.findAllByStatus(Order.Status.NEW, PageRequest.of(0, 20)))
                .thenReturn(Flux.fromIterable(content));
        when(orderRepository.countByStatus(Order.Status.NEW)).thenReturn(Mono.just((long) content.size()));

        StepVerifier.create(orderService.getOrders(Order.Status.NEW, 0, 20))
                .assertNext(page -> {
                    assertEquals(2, page.getContent().size());
                    assertEquals(0, page.getCurrentPage());
                    assertEquals(2, page.getNumberOfElements());
                    assertEquals(2, page.getTotalElements());
                })
                .verifyComplete();
    }

    @Test
    void changeOrderStatus() {
    }

    @Test
    void setCourier() {
    }
}