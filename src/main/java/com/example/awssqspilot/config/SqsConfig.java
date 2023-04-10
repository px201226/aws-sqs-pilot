package com.example.awssqspilot.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.support.AcknowledgmentHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;

@Getter
@Configuration
@RequiredArgsConstructor
public class SqsConfig {

	private final AWSCredentialsProvider awsCredentialsProvider;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Bean
	@Primary
	public AmazonSQSAsync amazonSQSAsync() {
		return AmazonSQSAsyncClientBuilder.standard().withCredentials(awsCredentialsProvider)
				.withRegion(region)
				.build();
	}

	@Bean
	public QueueMessageHandlerFactory queueMessageHandlerFactory() {
		QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();

//		factory.setArgumentResolvers(Collections.singletonList(new PayloadMethodArgumentResolver(mappingJackson2MessageConverter())));
		return factory;
	}



}