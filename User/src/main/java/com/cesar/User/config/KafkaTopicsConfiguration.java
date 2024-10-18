package com.cesar.User.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaTopicsConfiguration {

    @Bean
    public NewTopic conversationCreatedTopic() {
        return TopicBuilder.name("UserUpdated")
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic conversationDeletedTopic() {
        return TopicBuilder.name("UserDeleted")
                .partitions(1)
                .replicas(1)
                .build();
    }
}