package com.guava.guavaparcel.dto.view;

import com.guava.guavaparcel.model.Order;

import java.util.UUID;

public record CancelOrderView(
        UUID id,
        Order.Status status
) {
}
