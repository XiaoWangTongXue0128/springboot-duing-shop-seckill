package com.duing.config;


import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@PropertySource("classpath:redis.properties")
public class RedisConfig {

    /***
     * 设置  redis存储数据时的 序列化方式
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 声明对 key 和 value的序列化方式
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericFastJsonRedisSerializer valueSerializer = new GenericFastJsonRedisSerializer();
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);

        // 除以上  spring-data 提供了多种序列化方式
//        GenericToStringSerializer
//        Jackson2JsonRedisSerializer

        return redisTemplate;
    }
}
