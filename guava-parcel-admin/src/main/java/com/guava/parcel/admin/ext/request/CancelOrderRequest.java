package com.guava.parcel.admin.ext.request;

import java.util.UUID;

public record CancelOrderRequest(
        UUID orderId
) {
}
