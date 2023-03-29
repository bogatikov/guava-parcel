package com.guava.parcel.admin.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
class ObjectStringConverter {
    private final ObjectMapper objectMapper;

    public <T> Mono<T> stringToObject(String data, Class<T> clazz) {
        return Mono.fromCallable(() -> objectMapper.readValue(data, clazz));
    }

    public <T> Mono<String> objectToString(T object) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(object));
    }
}
