package com.guava.parcel.admin.ext.response;

import com.guava.parcel.admin.model.Status;

import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        UUID courierId,
        String sourceAddress,
        String destinationAddress,
        Status status,
        Instant updatedAt,
        Instant createdAt
) {
}
