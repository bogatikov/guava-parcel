package com.guava.parcel.delivery.dto.form;

import com.guava.parcel.delivery.model.Order;

import java.util.UUID;

public record ChangeOrderStatusForm(
        UUID orderId,
        Order.Status status
) {
}
