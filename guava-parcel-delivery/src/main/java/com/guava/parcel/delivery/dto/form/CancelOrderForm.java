package com.guava.parcel.delivery.dto.form;

import java.util.UUID;

public record CancelOrderForm(
        UUID orderId
) {
}
