package com.guava.parcel.courier.kafka;

public record KafkaEvent(
        String topic,
        String key,
        String message
) {
}