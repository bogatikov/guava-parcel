package com.guava.guavaparcel.dto.form;

import java.util.UUID;

public record CancelOrderForm(
        UUID orderId
) {
}
