package com.guava.parcel.user.dto.view;


import com.guava.parcel.user.model.Status;

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
