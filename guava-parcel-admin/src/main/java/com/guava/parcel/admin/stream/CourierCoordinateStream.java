package com.guava.parcel.admin.stream;

import com.guava.parcel.admin.event.CourierCoordinateEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class CourierCoordinateStream {
    private final Sinks.Many<CourierCoordinateEvent> courierCoordinateSink = Sinks.many().multicast().directBestEffort();

    public Flux<CourierCoordinateEvent> getStream() {
        return courierCoordinateSink.asFlux();
    }

    public Mono<Void> publishToStream(CourierCoordinateEvent courierCoordinateEvent) {
        return Mono.fromCallable(() -> courierCoordinateSink.tryEmitNext(courierCoordinateEvent))
                .then();
    }
}
