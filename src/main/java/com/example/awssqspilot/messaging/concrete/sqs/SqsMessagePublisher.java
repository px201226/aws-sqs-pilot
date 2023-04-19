package com.example.awssqspilot.messaging.concrete.sqs;

import com.example.awssqspilot.messaging.channel.MessageChannel;
import com.example.awssqspilot.messaging.message.MessagePublisher;
import com.example.awssqspilot.messaging.message.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsMessagePublisher<T extends SqsMessage> implements MessagePublisher<Message<SqsMessage>, Boolean> {

	private final QueueMessagingTemplate queueMessagingTemplate;
	private final SqsChannelResolver resolver;

	@Override public boolean isSupportChannel(final MessageChannel messageChannel) {
		return resolver.isSupportChannel(messageChannel);
	}

	@Override public MessageSender<Message<SqsMessage>, Boolean> getMessageSender() {
		return (channel, message) -> {
			queueMessagingTemplate.convertAndSend(
					resolver.resolve(channel),
					message.getPayload(),
					message.getHeaders()
			);

//			log.info("send message : {}", message);
			return true;
		};
	}

	@Override public Boolean send(final MessageChannel messageChannel, final Message<SqsMessage> message) {
		return MessagePublisher.super.send(messageChannel, message);
	}
}
