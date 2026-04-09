package com.example.nlayered.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Explicit Kafka configuration for Spring Kafka 4.x / Spring Boot 4.x.
 *
 * Spring Framework 7.x enforces strict generic-type matching, so the auto-configured
 * KafkaTemplate&lt;?,?&gt; cannot satisfy KafkaTemplate&lt;String,Object&gt; injection
 * points. Defining all typed beans here avoids that mismatch and gives full
 * control over producer/consumer settings.
 */
@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.order-events}")
    private String orderEventsTopic;

    @Value("${kafka.topics.customer-events}")
    private String customerEventsTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    // ── Topics ────────────────────────────────────────────────────────────

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name(orderEventsTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic customerEventsTopic() {
        return TopicBuilder.name(customerEventsTopic).partitions(3).replicas(1).build();
    }

    // ── Producer ──────────────────────────────────────────────────────────

    @Bean
    @SuppressWarnings("removal")
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,     bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG,                   "all");
        props.put(ProducerConfig.RETRIES_CONFIG,                3);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS,         false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // ── Consumer ──────────────────────────────────────────────────────────

    @Bean
    @SuppressWarnings("removal")
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,           consumerGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,  "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES,
                  "com.example.nlayered.events.model");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS,   false);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            RecordMessageConverter messageConverter) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setRecordMessageConverter(messageConverter);
        return factory;
    }

    @Bean
    @SuppressWarnings("removal")
    public RecordMessageConverter messageConverter() {
        return new JsonMessageConverter();
    }
}