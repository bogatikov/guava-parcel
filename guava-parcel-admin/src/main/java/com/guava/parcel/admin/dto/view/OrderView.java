package com.guava.parcel.admin.dto.view;

import com.guava.parcel.admin.model.Status;

import java.time.Instant;
import java.util.UUID;

public record OrderView(
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
