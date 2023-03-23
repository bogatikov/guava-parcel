package com.guava.parcel.admin.ext.response;

import com.guava.parcel.admin.model.Status;

import java.time.Instant;
import java.util.UUID;

public record OrderShortResponse(
        UUID id,
        UUID userId,
        Status status,
        Instant updatedAt,
        Instant createdAt
) {
}
