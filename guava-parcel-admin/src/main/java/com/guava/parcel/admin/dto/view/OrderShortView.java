package com.guava.parcel.admin.dto.view;

import com.guava.parcel.admin.model.Status;

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
