package com.guava.guavaparcel.dto.view;

import java.util.UUID;

public record SetCourierForm(
        UUID orderId,
        UUID courierId
) {
}
