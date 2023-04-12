package com.example.awssqspilot.springboot.event.sqs.message;

import com.example.awssqspilot.event.channel.MessageChannel;
import com.example.awssqspilot.event.message.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsMessagePublisher<T> implements MessagePublisher<Message<T>> {

	private final QueueMessagingTemplate queueMessagingTemplate;
	private final SqsChannelResolver resolver;

	@Override public void send(final MessageChannel channel, final Message<T> message) {
		queueMessagingTemplate.convertAndSend(
				resolver.resolve(channel),
				message.getPayload(),
				message.getHeaders()
		);
	}


}
