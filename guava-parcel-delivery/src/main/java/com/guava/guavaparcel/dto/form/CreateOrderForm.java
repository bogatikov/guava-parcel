package com.guava.guavaparcel.dto.form;

import java.util.UUID;

public record CreateOrderForm(
        String sourceAddress,
        String destinationAddress,
        UUID userId
) {
}
