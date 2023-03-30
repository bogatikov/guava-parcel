package com.guava.parcel.user.kafka;

public record KafkaEvent(
        String topic,
        String key,
        String message
) {
}