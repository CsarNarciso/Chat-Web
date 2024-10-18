package com.cesar.User.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaTopicConfiguration {

    @Bean
    public NewTopic userUpdatedTopic() {
        return TopicBuilder.name("UserUpdated")
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic userDeletedTopic() {
        return TopicBuilder.name("UserDeleted")
                .partitions(2)
                .replicas(1)
                .build();
    }
}