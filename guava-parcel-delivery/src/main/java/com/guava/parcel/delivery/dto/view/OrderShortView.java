package com.guava.parcel.delivery.dto.view;

import com.guava.parcel.delivery.model.Order;

import java.time.Instant;
import java.util.UUID;

public record OrderShortView(
        UUID id,
        UUID userId,
        Order.Status status,
        Instant updatedAt,
        Instant createdAt
) {
}
