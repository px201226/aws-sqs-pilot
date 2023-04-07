package com.example.awssqspilot.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SqsConsumeConfig {

	private final AmazonSQSAsync amazonSQSAsync;

	@Bean
	public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory() {
		final SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
		factory.setAmazonSqs(amazonSQSAsync);
		factory.setMaxNumberOfMessages(10);
		factory.setWaitTimeOut(20);// Long polling 설정 0~20
		return factory;
	}

}