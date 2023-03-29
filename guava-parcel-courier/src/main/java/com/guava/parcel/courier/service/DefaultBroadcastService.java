package com.guava.parcel.courier.service;

import com.guava.parcel.courier.config.TopicConstants;
import com.guava.parcel.courier.event.CourierCoordinateEvent;
import com.guava.parcel.courier.kafka.KafkaEventPublisherSink;
import com.guava.parcel.courier.service.api.BroadcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultBroadcastService implements BroadcastService {

    private final KafkaEventPublisherSink eventPublisherSink;

    @Override
    public Mono<Void> broadcastCourierCoordinateEvent(CourierCoordinateEvent courierCoordinateEvent) {
        return eventPublisherSink.convertAndEmit(
                courierCoordinateEvent,
                TopicConstants.COURIER_COORDINATE,
                courierCoordinateEvent.courierId().toString()
        );
    }
}
