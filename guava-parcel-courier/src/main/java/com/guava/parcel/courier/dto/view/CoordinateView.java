package com.guava.parcel.courier.dto.view;

import java.util.UUID;

public record CoordinateView(
        UUID courierId,
        Double longitude,
        Double latitude
) {
}
