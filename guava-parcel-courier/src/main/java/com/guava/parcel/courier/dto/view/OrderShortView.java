package com.guava.parcel.courier.dto.view;

import com.guava.parcel.courier.model.Status;

import java.time.Instant;
import java.util.UUID;

public record OrderShortView(
        UUID id,
        UUID userId,
        Status status,
        Instant updatedAt,
        Instant createdAt
) {
}
