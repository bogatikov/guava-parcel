package com.guava.parcel.user.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReactiveKafkaConfiguration {

    @Bean
    public KafkaReceiver<String, String> kafkaReceiver(ReceiverOptions<String, String> kafkaReceiverOptions) {
        return KafkaReceiver.create(kafkaReceiverOptions);
    }

    @Bean
    public KafkaSender<String, String> kafkaSender(SenderOptions<String, String> kafkaSenderOptions) {
        return KafkaSender.create(kafkaSenderOptions);
    }

    @Bean
    ReceiverOptions<String, String> kafkaReceiverOptions(KafkaConfig kafkaConfig) {
        ReceiverOptions<String, String> options = ReceiverOptions.create(kafkaConsumerConfiguration(kafkaConfig));
        return options
                .subscription(kafkaConfig.subscribeTopics)
                .commitInterval(kafkaConfig.commitInterval)
                .commitBatchSize(kafkaConfig.commitBatchSize)
                .withKeyDeserializer(new StringDeserializer())
                .withValueDeserializer(new StringDeserializer());
    }

    @Bean
    SenderOptions<String, String> kafkaSenderOptions(KafkaConfig kafkaConfig) {
        SenderOptions<String, String> options = SenderOptions.create(kafkaProducerConfiguration(kafkaConfig));
        return options
                .withKeySerializer(new StringSerializer())
                .withValueSerializer(new StringSerializer())
                .stopOnError(true);
    }

    private Map<String, Object> kafkaConsumerConfiguration(KafkaConfig kafkaConfig) {
        var configuration = new HashMap<String, Object>();
        configuration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        configuration.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroupId());
        configuration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getAutoOffsetReset());
        return configuration;
    }

    private Map<String, Object> kafkaProducerConfiguration(
            KafkaConfig kafkaConfig
    ) {
        var configuration = new HashMap<String, Object>();
        configuration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        return configuration;
    }
}
