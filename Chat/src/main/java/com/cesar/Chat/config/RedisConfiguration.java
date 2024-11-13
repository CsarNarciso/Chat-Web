package com.cesar.Chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.cesar.Chat.dto.ConversationDTO;
import com.cesar.Chat.dto.MessageDTO;
import com.cesar.Chat.dto.ParticipantDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfiguration {
    
    @Bean
    RedisTemplate<String, Object> genericRedisTemplate(){
        return createTemplate(Object.class);
    }

    @Bean
    RedisTemplate<String, ConversationDTO> conversationRedisTemplate(){
        return createTemplate(ConversationDTO.class);
    }

    @Bean
    RedisTemplate<String, ParticipantDTO> participantRedisTemplate(){
    	return createTemplate(ParticipantDTO.class);
    }
    
    @Bean
    RedisTemplate<String, MessageDTO> messageRedisTemplate(){
    	return createTemplate(MessageDTO.class);
    }

    
    
    
    
    @Bean
    JedisConnectionFactory connectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(REDIS_HOSTNAME, REDIS_PORT);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }
    
    
    private <T> RedisTemplate<String, T> createTemplate(Class<T> type) {
    	
    	RedisTemplate<String, T> template = new RedisTemplate<>();
    	
    	template.setConnectionFactory(connectionFactory());
    	
    	StringRedisSerializer keySerializer = new StringRedisSerializer();
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        
        Jackson2JsonRedisSerializer<T> valueSerializer = new Jackson2JsonRedisSerializer<>(mapper, type);
        template.setValueSerializer(valueSerializer);
        
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        
        return template;
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