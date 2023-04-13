package com.example.awssqspilot.springboot.messaging.sqs;

import com.example.awssqspilot.messaging.channel.MessageChannel;
import com.example.awssqspilot.messaging.message.MessagePublisher;
import com.example.awssqspilot.messaging.message.MessageSender;
import com.example.awssqspilot.messaging.message.OrderingEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsMessagePublisher<T extends OrderingEventMessage> implements MessagePublisher<OrderingEventMessage, Boolean> {

	private final QueueMessagingTemplate queueMessagingTemplate;
	private final SqsChannelResolver resolver;

	@Override public boolean isSupportChannel(final MessageChannel messageChannel) {
		return resolver.isSupportChannel(messageChannel);
	}

	@Override public MessageSender<OrderingEventMessage, Boolean> getMessageSender() {
		return (channel, message) -> {

			final var sqsMessage = SqsMessageBuilder.from(message);

			queueMessagingTemplate.convertAndSend(
					resolver.resolve(channel),
					sqsMessage.getPayload(),
					sqsMessage.getHeaders()
			);

			log.info("send message : {}", message);
			return true;
		};
	}


}
