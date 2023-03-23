package com.guava.guavaparcel.dto.form;

import java.util.UUID;

public record SetCourierForm(
        UUID orderId,
        UUID courierId
) {
}
