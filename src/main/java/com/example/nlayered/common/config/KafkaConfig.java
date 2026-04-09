package com.example.nlayered.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.order-events}")
    private String orderEventsTopic;

    @Value("${kafka.topics.customer-events}")
    private String customerEventsTopic;

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name(orderEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic customerEventsTopic() {
        return TopicBuilder.name(customerEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public RecordMessageConverter messageConverter() {
        return new JsonMessageConverter();
    }
}