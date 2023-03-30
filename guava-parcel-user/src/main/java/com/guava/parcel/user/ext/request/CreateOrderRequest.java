package com.guava.parcel.user.ext.request;

import java.util.UUID;

public record CreateOrderRequest(
        String sourceAddress,
        String destinationAddress,
        UUID userId
) {
}
