package com.guava.parcel.delivery.dto.form;

import java.util.UUID;

public record CreateOrderForm(
        String sourceAddress,
        String destinationAddress,
        UUID userId
) {
}
