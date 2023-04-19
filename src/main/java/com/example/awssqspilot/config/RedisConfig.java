package com.example.awssqspilot.config;

import com.example.awssqspilot.domain.event.ApplicationEvent;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.core.convert.MappingConfiguration;
import org.springframework.data.redis.core.index.IndexConfiguration;
import org.springframework.data.redis.core.mapping.RedisMappingContext;

@RedisHash(timeToLive = 10L)
@Configuration
public class RedisConfig {

	@Value("${redis.event.host}")
	private String host;

	@Value("${redis.event.port}")
	private int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	public RedisMappingContext keyValueMappingContext() {
		return new RedisMappingContext(new MappingConfiguration(new IndexConfiguration(), new MyKeyspaceConfiguration()));
	}

	public static class MyKeyspaceConfiguration extends KeyspaceConfiguration {

		@Override
		protected Iterable<KeyspaceSettings> initialConfiguration() {
			KeyspaceSettings keyspaceSettings = new KeyspaceSettings(ApplicationEvent.class, "ApplicationEvent");
			keyspaceSettings.setTimeToLive(3600L);
			return Collections.singleton(keyspaceSettings);
		}
	}
}