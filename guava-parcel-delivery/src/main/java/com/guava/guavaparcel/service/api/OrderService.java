package com.guava.guavaparcel.service.api;

import com.guava.guavaparcel.dto.form.CancelOrderForm;
import com.guava.guavaparcel.dto.form.ChangeDestinationForm;
import com.guava.guavaparcel.dto.form.ChangeOrderStatusForm;
import com.guava.guavaparcel.dto.form.CreateOrderForm;
import com.guava.guavaparcel.dto.view.OrderShortView;
import com.guava.guavaparcel.dto.view.OrderView;
import com.guava.guavaparcel.dto.view.SetCourierForm;
import com.guava.guavaparcel.model.Order;
import com.guava.guavaparcel.model.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderService {

    Mono<OrderView> createOrder(CreateOrderForm createOrderForm);

    Mono<OrderView> cancelOrder(CancelOrderForm cancelOrderForm);

    Mono<OrderView> changeDestination(ChangeDestinationForm changeDestinationForm);

    Mono<Page<OrderShortView>> getOrders(@NonNull UUID userId, @NonNull Integer page, @NonNull Integer size);

    Mono<OrderView> getOrder(UUID orderId);

    Mono<OrderView> changeOrderStatus(ChangeOrderStatusForm changeOrderStatusForm);

    Mono<Page<OrderShortView>> getOrders(@Nullable Order.Status status, @NonNull Integer page, @NonNull Integer size);

    Mono<OrderView> setCourier(SetCourierForm setCourierForm);
}
