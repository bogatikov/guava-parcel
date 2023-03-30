package com.guava.guavaparcel.service.api;

import com.guava.guavaparcel.dto.form.CancelOrderForm;
import com.guava.guavaparcel.dto.form.ChangeDestinationForm;
import com.guava.guavaparcel.dto.form.ChangeOrderStatusForm;
import com.guava.guavaparcel.dto.form.CreateOrderForm;
import com.guava.guavaparcel.dto.form.SetCourierForm;
import com.guava.guavaparcel.dto.view.OrderShortView;
import com.guava.guavaparcel.dto.view.OrderView;
import com.guava.guavaparcel.model.Page;
import com.guava.guavaparcel.model.filter.OrderFilter;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderService {

    Mono<OrderView> createOrder(CreateOrderForm createOrderForm);

    Mono<OrderView> cancelOrder(CancelOrderForm cancelOrderForm);

    Mono<OrderView> changeDestination(ChangeDestinationForm changeDestinationForm);

    Mono<OrderView> getOrder(UUID orderId);

    Mono<OrderView> changeOrderStatus(ChangeOrderStatusForm changeOrderStatusForm);

    Mono<Page<OrderShortView>> getOrders(OrderFilter orderFilter, @NonNull Integer page, @NonNull Integer size);

    Mono<OrderView> setCourier(SetCourierForm setCourierForm);
}
