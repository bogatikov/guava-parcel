package com.guava.parcel.admin.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactivefeign.ReactiveOptions;
import reactivefeign.client.log.DefaultReactiveLogger;
import reactivefeign.client.log.ReactiveLoggerListener;
import reactivefeign.webclient.WebReactiveOptions;

import java.time.Clock;

@Configuration
@ConfigurationProperties("clients.feign-client")
@Data
@Slf4j
public class FeignClientConfig {
    private int readTimeout;
    private int writeTimeout;
    private int connectTimeout;
    private int responseTimeout;
    private int maxRetry;

    @Bean
    public ReactiveLoggerListener loggerListener() {
        return new DefaultReactiveLogger(Clock.systemUTC(), LoggerFactory.getLogger(FeignClientConfig.class.getName()));
    }

    @Bean
    public ReactiveOptions reactiveOptions() {
        return new WebReactiveOptions.Builder()
                .setReadTimeoutMillis(readTimeout)
                .setWriteTimeoutMillis(writeTimeout)
                .setResponseTimeoutMillis(responseTimeout)
                .setConnectTimeoutMillis(connectTimeout)
                .build();
    }
}
