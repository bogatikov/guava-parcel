package com.guava.parcel.admin.ext.request;

import java.util.UUID;

public record SetCourierRequest(
        UUID orderId,
        UUID courierId
) {
}
