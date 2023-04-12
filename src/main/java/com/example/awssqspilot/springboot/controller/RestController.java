package com.example.awssqspilot.springboot.controller;


import com.example.awssqspilot.event.channel.MessageChannel;
import com.example.awssqspilot.event.message.MessageBuilder;
import com.example.awssqspilot.event.message.MessagePublisher;
import com.example.awssqspilot.springboot.event.sqs.message.SqsMessageBuilderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
public class RestController {

	private final MessagePublisher springEventPublisher;
	public static final String FIFO_QUEUE_NAME = "async_ff.fifo";

	@PostMapping("/api/post/foo")
	public void send(@RequestBody Foo foo) {
		final var foo1 = new Foo("123", 1);
		final var messageGroupBuilder = SqsMessageBuilderFactory.messageGroupBuilder();

		springEventPublisher.send(MessageChannel.SPRING_EVENT_CHANNEL, MessageBuilder.build(
				messageGroupBuilder.setPayload(foo1).make()
		));

		springEventPublisher.send(MessageChannel.SPRING_EVENT_CHANNEL, MessageBuilder.build(
				messageGroupBuilder.setPayload(foo1).make()
		));
	}

	@PostMapping("/api/post/bar")
	public void send(@RequestBody Bar bar) {

	}
}
