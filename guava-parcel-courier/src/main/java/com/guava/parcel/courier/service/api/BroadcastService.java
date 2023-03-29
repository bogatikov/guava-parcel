package com.guava.parcel.courier.service.api;

import com.guava.parcel.courier.event.CourierCoordinateEvent;
import reactor.core.publisher.Mono;

public interface BroadcastService {
    Mono<Void> broadcastCourierCoordinateEvent(CourierCoordinateEvent courierCoordinateEvent);
}
