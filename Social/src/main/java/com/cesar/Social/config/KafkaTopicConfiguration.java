package com.cesar.Social.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaTopicConfiguration {

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("")
                .partitions(1)
                .replicas(1)
                .build();
    }
}