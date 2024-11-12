package com.cesar.Chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.MessageDTO;
import com.cesar.Chat.dto.ParticipantDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfiguration {
    

    @Bean
    public JedisConnectionFactory connectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(REDIS_HOSTNAME, REDIS_PORT);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }
    
    
    @Bean
    public RedisTemplate<String, Object> genericRedisTemplate(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        injectCommonConfigs(template);
        return template;
    }

    @Bean
    public RedisTemplate<String, ConversationDTO> conversationRedisTemplate(){
        RedisTemplate<String, ConversationDTO> template = new RedisTemplate<>();
        injectCommonConfigs(template);
        return template;
    }

    @Bean
    public RedisTemplate<String, ParticipantDTO> participantRedisTemplate(){
        RedisTemplate<String, ParticipantDTO> template = new RedisTemplate<>();
        injectCommonConfigs(template);
        return template;
    }
    
    @Bean
    public RedisTemplate<String, MessageDTO> messageRedisTemplate(){
        RedisTemplate<String, MessageDTO> template = new RedisTemplate<>();
        injectCommonConfigs(template);
        return template;
    }
    
    
    
    
    
    private void injectCommonConfigs(RedisTemplate<?, ?> template ) {
    	template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
    }
    
    
    
    
    public RedisConfiguration(ObjectMapper mapper) {
        //Enable JavaTimeModule in ObjectMapper for Redis serializer (for LocalDateTime support)
    	this.mapper = mapper;
    	mapper.registerModule(new JavaTimeModule());
    }
    
    @Value("${redis.hostname}")
    private String REDIS_HOSTNAME;
    @Value("${redis.port}")
    private Integer REDIS_PORT;
    private final ObjectMapper mapper;
}