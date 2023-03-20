package com.guava.guavaparcel.dto.form;

import com.guava.guavaparcel.model.Order;

import java.util.UUID;

public record ChangeOrderStatusForm(
        UUID orderId,
        Order.Status status
) {
}
