package com.example.awssqspilot.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.core.env.ResourceIdResolver;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

@Configuration
@RequiredArgsConstructor
public class SqsPublishConfig {

	private final AmazonSQSAsync amazonSQSAsync;
	private final ObjectMapper objectMapper;

	@Bean
	public QueueMessagingTemplate queueMessagingTemplate() {
		return new QueueMessagingTemplate(amazonSQSAsync, (ResourceIdResolver) null, messageConverter());
//		return new QueueMessagingTemplate(amazonSQSAsync);
	}

	@Bean
	public StringHttpMessageConverter stringHttpMessageConverter() {
		return new StringHttpMessageConverter(StandardCharsets.UTF_8);
	}

	@Bean
	public MessageConverter messageConverter() {
		final var converter = new MappingJackson2MessageConverter();
		converter.setObjectMapper(objectMapper);
		converter.setSerializedPayloadClass(String.class);
		converter.setStrictContentTypeMatch(false);
		return converter;
	}
}
