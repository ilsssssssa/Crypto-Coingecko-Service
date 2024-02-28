package com.vtxlab.project.bc_crypto_coingecko.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

  @Bean
  RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory,
      ObjectMapper redisObjectMapper) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(factory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(
        new GenericJackson2JsonRedisSerializer(redisObjectMapper));
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }

  @Bean
  RedisHelper redisProfileHelper(RedisConnectionFactory factory, //
      ObjectMapper redisObjectMapper) {
    return new RedisHelper(factory, redisObjectMapper);
  }
  // @Bean
  // public RedisHelper2<Object> redisHelper(RedisConnectionFactory factory) {
  // RedisTemplate<String, Object> redisTemplate = RedisHelper2.template(factory, Object.class);
  // return new RedisHelper2<>(redisTemplate);
  // }
}
