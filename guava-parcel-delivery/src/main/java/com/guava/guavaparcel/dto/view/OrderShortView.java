package com.guava.guavaparcel.dto.view;

import com.guava.guavaparcel.model.Order;

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
