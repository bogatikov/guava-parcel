package com.guava.parcel.user.dto.form;

import java.util.UUID;

public record CancelOrderForm(
        UUID orderId
) {
}
