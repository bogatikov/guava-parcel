package com.guava.parcel.user.ext.request;

import java.util.UUID;

public record ChangeDestinationRequest(
        UUID orderId,
        String destinationAddress
) {
}
