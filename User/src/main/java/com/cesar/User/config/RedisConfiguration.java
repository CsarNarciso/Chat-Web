package com.cesar.User.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.cesar.User.entity.User;

@Configuration
public class RedisConfiguration {

    @Value("${redis.hostname}")
    private String REDIS_HOSTNAME;
    @Value("${redis.port}")
    private int REDIS_PORT;

    @Bean
    public JedisConnectionFactory connectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(REDIS_HOSTNAME, REDIS_PORT);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }
	
	@Bean
    public RedisTemplate<String, User> userRedisTemplate(){
        RedisTemplate<String, User> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
}
