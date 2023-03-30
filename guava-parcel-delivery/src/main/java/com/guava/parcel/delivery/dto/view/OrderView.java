package com.guava.parcel.delivery.dto.view;

import com.guava.parcel.delivery.model.Order;

import java.time.Instant;
import java.util.UUID;

public record OrderView(
        UUID id,
        UUID userId,
        UUID courierId,
        String sourceAddress,
        String destinationAddress,
        Order.Status status,
        Instant updatedAt,
        Instant createdAt
) {
}
