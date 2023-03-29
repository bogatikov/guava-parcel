package com.guava.parcel.courier.event;

import java.util.UUID;

public record CourierCoordinateEvent(
        UUID courierId,
        Double longitude,
        Double latitude
) {
}
