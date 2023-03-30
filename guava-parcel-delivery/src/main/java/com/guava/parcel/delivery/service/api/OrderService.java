package com.guava.parcel.delivery.service.api;

import com.guava.parcel.delivery.dto.form.CancelOrderForm;
import com.guava.parcel.delivery.dto.form.ChangeDestinationForm;
import com.guava.parcel.delivery.dto.form.ChangeOrderStatusForm;
import com.guava.parcel.delivery.dto.form.CreateOrderForm;
import com.guava.parcel.delivery.dto.form.SetCourierForm;
import com.guava.parcel.delivery.dto.view.OrderShortView;
import com.guava.parcel.delivery.dto.view.OrderView;
import com.guava.parcel.delivery.model.Page;
import com.guava.parcel.delivery.model.filter.OrderFilter;
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
