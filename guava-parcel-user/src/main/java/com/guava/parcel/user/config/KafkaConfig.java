package com.guava.parcel.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
public class KafkaConfig {
    private List<String> bootstrapServers;
    private String groupId;
    /*
     *  What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server (e.g. because that data has been deleted):
     *  earliest: automatically reset the offset to the earliest offset
     *  latest: automatically reset the offset to the latest offset
     *  none: throw exception to the consumer if no previous offset is found for the consumer's group
     *  https://stackoverflow.com/questions/32390265/what-determines-kafka-consumer-offset
     */
    private String autoOffsetReset = "latest";

    Duration commitInterval = Duration.ofSeconds(5);
    Integer commitBatchSize = 10000;
    List<String> subscribeTopics = new ArrayList<>();
}
