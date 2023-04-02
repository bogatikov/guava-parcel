package com.guava.parcel.delivery.service;

import com.guava.parcel.delivery.dto.form.CancelOrderForm;
import com.guava.parcel.delivery.dto.form.ChangeDestinationForm;
import com.guava.parcel.delivery.dto.form.CreateOrderForm;
import com.guava.parcel.delivery.dto.view.OrderView;
import com.guava.parcel.delivery.error.EntityNotFound;
import com.guava.parcel.delivery.model.Order;
import com.guava.parcel.delivery.repository.CustomOrderRepository;
import com.guava.parcel.delivery.repository.OrderRepository;
import com.guava.parcel.delivery.service.api.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultOrderServiceTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private CustomOrderRepository customOrderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        customOrderRepository = mock(CustomOrderRepository.class);
        orderService = new DefaultOrderService(orderRepository, customOrderRepository);
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
        // TODO: 02.04.2023
    }

    @Test
    void changeDestination() {
        // TODO: 02.04.2023
    }

    @Test
    void getOrdersByStatusShouldReturnEmptyPage() {
        // TODO: 02.04.2023
    }

    @Test
    void changeOrderStatus() {
        // TODO: 02.04.2023
    }

    @Test
    void setCourier() {
        // TODO: 02.04.2023
    }

    @Test
    void getCourierStats() {
        UUID courierId = UUID.randomUUID();
        when(customOrderRepository.getCourierStatsByCourierId(courierId))
                .thenReturn(Mono.just(Map.of(Order.Status.NEW, 2, Order.Status.FINISHED, 3)));

        StepVerifier.create(orderService.getCourierStats(courierId))
                .assertNext(courierStatsView -> {
                    assertEquals(courierId, courierStatsView.getCourierId());
                    assertNotNull(courierStatsView.getStats());
                    assertNotNull(courierStatsView.getStats().get(Order.Status.NEW));
                    assertNotNull(courierStatsView.getStats().get(Order.Status.FINISHED));
                    assertEquals(2, courierStatsView.getStats().get(Order.Status.NEW));
                    assertEquals(3, courierStatsView.getStats().get(Order.Status.FINISHED));
                })
                .verifyComplete();
    }

    @Test
    void getCourierStatsWithNoOrders() {
        UUID courierId = UUID.randomUUID();
        when(customOrderRepository.getCourierStatsByCourierId(courierId))
                .thenReturn(Mono.empty());

        StepVerifier.create(orderService.getCourierStats(courierId))
                .assertNext(courierStatsView -> {
                    assertEquals(courierId, courierStatsView.getCourierId());
                    assertNotNull(courierStatsView.getStats());
                })
                .verifyComplete();
    }
}