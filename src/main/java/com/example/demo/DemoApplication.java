package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 资源池中最大连接数
        poolConfig.setMaxTotal(500);
        // 资源池允许最大空闲的连接数
        poolConfig.setMaxIdle(500);
        // 当资源池连接用尽后，调用者的最大等待时间(单位为毫秒)
        poolConfig.setMaxWaitMillis(10000);
        // 	是否开启空闲资源监测
        poolConfig.setTestWhileIdle(true);
        return poolConfig;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig poolConfig) {
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
//        standaloneConfiguration.setHostName("198.20.8.226");

        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jpcf =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        jpcf.poolConfig(poolConfig);

        return new JedisConnectionFactory(standaloneConfiguration, jpcf.build());
    }


}
