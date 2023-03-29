package com.guava.parcel.admin.event;

import java.util.UUID;

public record CourierCoordinateEvent(
        UUID courierId,
        Double longitude,
        Double latitude
) {
}
