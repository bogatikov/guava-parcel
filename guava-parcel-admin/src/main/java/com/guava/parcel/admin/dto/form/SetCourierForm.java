package com.guava.parcel.admin.dto.form;

import java.util.UUID;

public record SetCourierForm(
        UUID orderId,
        UUID courierId
) {
}
