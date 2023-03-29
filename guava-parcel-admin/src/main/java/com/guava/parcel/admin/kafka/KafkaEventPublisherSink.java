package com.guava.parcel.admin.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisherSink {
    private final ObjectStringConverter objectStringConverter;
    private final Sinks.Many<KafkaEvent> sinks = Sinks.many().multicast().directBestEffort();

    private Mono<Void> emitEvent(KafkaEvent kafkaEvent) {
        return Mono.fromCallable(() -> sinks.tryEmitNext(kafkaEvent))
                .then();
    }

    public <T> Mono<Void> convertAndEmit(T obj, String topic, String key) {
        return objectStringConverter.objectToString(obj)
                .flatMap(serializedEvent -> {
                    log.info("Broadcast message {} to topic {}", serializedEvent, topic);
                    return emitEvent(new KafkaEvent(
                                    topic,
                                    key,
                                    serializedEvent
                            )
                    );
                });
    }

    public Flux<KafkaEvent> getEventPublisher() {
        return sinks.asFlux();
    }
}
