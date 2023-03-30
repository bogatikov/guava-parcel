package com.guava.parcel.user.dto.view;


import com.guava.parcel.user.model.Status;

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
