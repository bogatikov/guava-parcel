package com.guava.parcel.courier.ext.response;


import com.guava.parcel.courier.model.Status;

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
