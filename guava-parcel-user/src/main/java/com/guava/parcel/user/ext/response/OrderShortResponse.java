package com.guava.parcel.user.ext.response;



import com.guava.parcel.user.model.Status;

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
