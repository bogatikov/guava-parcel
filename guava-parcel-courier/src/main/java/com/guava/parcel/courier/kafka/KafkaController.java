package com.guava.parcel.courier.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaController {
    private final KafkaReceiver<String, String> reactiveKafkaReceiver;
    private final KafkaSender<String, String> reactiveKafkaSender;
    private final ObjectStringConverter objectStringConverter;
    private final KafkaEventPublisherSink kafkaEventPublisherSink;

    private final Scheduler kafkaEventHandlerScheduler = Schedulers.newBoundedElastic(
            Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE,
            Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
            "kafka-handler"
    );

    @EventListener(ApplicationReadyEvent.class)
    public void messagesPublisher() {
        reactiveKafkaSender.send(
                        kafkaEventPublisherSink.getEventPublisher()
                                .map(event -> {
                                    ProducerRecord<String, String> producerRecord;
                                    if (event.key() == null) {
                                        producerRecord = new ProducerRecord<>(event.topic(), event.message());
                                    } else {
                                        producerRecord = new ProducerRecord<>(event.topic(), event.key(), event.message());
                                    }
                                    return SenderRecord.create(
                                            producerRecord,
                                            event.topic()
                                    );
                                })
                )
                .subscribe(stringSenderResult -> {
                    RecordMetadata recordMetadata = stringSenderResult.recordMetadata();
                    log.info(
                            "Message {} sent successfully, topic-partition={}-{} offset={} timestamp={}",
                            stringSenderResult.correlationMetadata(),
                            recordMetadata.topic(),
                            recordMetadata.partition(),
                            recordMetadata.offset(),
                            recordMetadata.timestamp()
                    );
                });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onMessage() {
        Flux.defer(reactiveKafkaReceiver::receive)
                .subscribeOn(kafkaEventHandlerScheduler)
                .publishOn(kafkaEventHandlerScheduler)
                .cancelOn(kafkaEventHandlerScheduler)
                .groupBy(ConsumerRecord::key)
                .flatMap(group -> group.subscribeOn(kafkaEventHandlerScheduler)
                        .publishOn(kafkaEventHandlerScheduler)
                        .cancelOn(kafkaEventHandlerScheduler)
                        .concatMap(record -> handleRecord(record)
                                .doOnError(e -> log.error("Error kafka record handler", e))
                        )
                )
                .onErrorResume(e -> {
                            log.error("Listener Kafka Error", e);
                            return Mono.empty();
                        }
                )
                .repeat()
                .subscribe();
    }

    private Mono<Void> handleRecord(ReceiverRecord<String, String> record) {
        record.receiverOffset().acknowledge();
        log.info("Consume new record from ${record.topic()} partition-${record.partition()} offset-${record.offset()} with key ${record.key()} and value ${record.value()}");
        return Mono.empty();
    }

    private <T, R> Mono<R> convertAndPerform(
            ReceiverRecord<String, String> record,
            Class<T> clazz,
            Function<T, Mono<R>> function
    ) {
        return objectStringConverter.stringToObject(
                        record.value(),
                        clazz
                )
                .flatMap(function);
    }
}

