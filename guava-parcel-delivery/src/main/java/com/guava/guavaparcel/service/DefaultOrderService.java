package com.guava.guavaparcel.service;

import com.guava.guavaparcel.dto.form.CancelOrderForm;
import com.guava.guavaparcel.dto.form.ChangeDestinationForm;
import com.guava.guavaparcel.dto.form.ChangeOrderStatusForm;
import com.guava.guavaparcel.dto.form.CreateOrderForm;
import com.guava.guavaparcel.dto.form.SetCourierForm;
import com.guava.guavaparcel.dto.view.OrderShortView;
import com.guava.guavaparcel.dto.view.OrderView;
import com.guava.guavaparcel.error.EntityNotFound;
import com.guava.guavaparcel.model.Order;
import com.guava.guavaparcel.model.Page;
import com.guava.guavaparcel.repository.OrderRepository;
import com.guava.guavaparcel.service.api.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Mono<OrderView> createOrder(CreateOrderForm createOrderForm) {
        var newOrder = new Order(
                UUID.randomUUID(),
                createOrderForm.sourceAddress(),
                createOrderForm.destinationAddress(),
                createOrderForm.userId(),
                null,
                Order.Status.NEW,
                null,
                Instant.now(),
                1L,
                true
        );
        return orderRepository.save(newOrder)
                .map(this::mapOrderToOrderView);
    }

    @Override
    public Mono<OrderView> cancelOrder(CancelOrderForm cancelOrderForm) {
        return orderRepository.findById(cancelOrderForm.orderId())
                .filter(order -> order.getStatus() != Order.Status.FINISHED)
                .switchIfEmpty(Mono.error(new EntityNotFound("Order not found")))
                .doOnNext(order -> order.setStatus(Order.Status.CANCELED))
                .flatMap(orderRepository::save)
                .map(this::mapOrderToOrderView);
    }

    @Override
    public Mono<OrderView> changeDestination(ChangeDestinationForm changeDestinationForm) {
        return orderRepository.findById(changeDestinationForm.orderId())
                .switchIfEmpty(Mono.error(new EntityNotFound("Order not found")))
                .doOnNext(order -> order.setDestinationAddress(changeDestinationForm.destinationAddress()))
                .flatMap(orderRepository::save)
                .map(this::mapOrderToOrderView);
    }

    @Override
    public Mono<Page<OrderShortView>> getOrders(@NonNull UUID userId, @NonNull Integer page, @NonNull Integer size) {
        return orderRepository.findAllByUserId(userId, PageRequest.of(page, size))
                .map(this::mapOrderToOrderShortView)
                .collectList()
                .zipWith(orderRepository.countByUserId(userId))
                .map(ordersToCount -> new Page<>(
                                ordersToCount.getT1(),
                                page,
                                ordersToCount.getT2(),
                                ordersToCount.getT1().size()
                        )
                );
    }

    @Override
    public Mono<OrderView> getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new EntityNotFound("Order not found")))
                .map(this::mapOrderToOrderView);
    }

    @Override
    public Mono<OrderView> changeOrderStatus(ChangeOrderStatusForm changeOrderStatusForm) {
        return orderRepository.findById(changeOrderStatusForm.orderId())
                .switchIfEmpty(Mono.error(new EntityNotFound("Order not found")))
                .doOnNext(order -> order.setStatus(changeOrderStatusForm.status()))
                .flatMap(orderRepository::save)
                .map(order -> new OrderView(
                        order.getId(),
                        order.getUserId(),
                        order.getCourierId(),
                        order.getSourceAddress(),
                        order.getDestinationAddress(),
                        order.getStatus(),
                        order.getUpdatedAt(),
                        order.getCreatedAt())
                );
    }

    @Override
    public Mono<Page<OrderShortView>> getOrders(Order.Status status, @NonNull Integer page, @NonNull Integer size) {
        return orderRepository.findAllByStatus(status, PageRequest.of(page, size))
                .map(this::mapOrderToOrderShortView)
                .collectList()
                .zipWith(orderRepository.countByStatus(status))
                .map(ordersToCount -> new Page<>(ordersToCount.getT1(), page, ordersToCount.getT2(), ordersToCount.getT1().size()));
    }


    @Override
    public Mono<OrderView> setCourier(SetCourierForm setCourierForm) {
        return orderRepository.findById(setCourierForm.orderId())
                .switchIfEmpty(Mono.error(new EntityNotFound("Order not found")))
                .doOnNext(order -> order.setCourierId(setCourierForm.courierId()))
                .flatMap(orderRepository::save)
                .map(this::mapOrderToOrderView);
    }

    private OrderView mapOrderToOrderView(Order order) {
        return new OrderView(
                order.getId(),
                order.getUserId(),
                order.getCourierId(),
                order.getSourceAddress(),
                order.getDestinationAddress(),
                order.getStatus(),
                order.getUpdatedAt(),
                order.getCreatedAt()
        );
    }

    private OrderShortView mapOrderToOrderShortView(Order order) {
        return new OrderShortView(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getUpdatedAt(),
                order.getCreatedAt()
        );
    }
}
