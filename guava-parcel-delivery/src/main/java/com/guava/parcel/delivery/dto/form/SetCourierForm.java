package com.guava.parcel.delivery.dto.form;

import java.util.UUID;

public record SetCourierForm(
        UUID orderId,
        UUID courierId
) {
}
