package com.guava.parcel.user.ext.response;



import com.guava.parcel.user.model.Status;

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
