package com.example.awssqspilot.controller;


import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
public class RestController {

	private final QueueMessagingTemplate queueMessagingTemplate;
	public static final String FIFO_QUEUE_NAME = "async_ff.fifo";

	@PostMapping("/api/post/foo")
	public void send(@RequestBody Foo foo) {

		log.info("request body : {}", foo.toString());

		final var message = MessageBuilder.withPayload(foo)
				.setHeader(SqsMessageHeaders.SQS_GROUP_ID_HEADER, "group1")
				.setHeader(SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER, LocalDateTime.now().toString())
				.build();

		queueMessagingTemplate.convertAndSend(FIFO_QUEUE_NAME, message.getPayload(), message.getHeaders());


	}

	@PostMapping("/api/post/bar")
	public void send(@RequestBody Bar bar) {

		log.info("request body : {}", bar.toString());

		final var message = MessageBuilder.withPayload(bar)
				.setHeader(SqsMessageHeaders.SQS_GROUP_ID_HEADER, "group1")
				.setHeader(SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER, LocalDateTime.now().toString())
				.build();

		queueMessagingTemplate.convertAndSend(FIFO_QUEUE_NAME, message.getPayload(), message.getHeaders());


	}
}
