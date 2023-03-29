package com.guava.parcel.admin.kafka;

public record KafkaEvent(
        String topic,
        String key,
        String message
) {
}