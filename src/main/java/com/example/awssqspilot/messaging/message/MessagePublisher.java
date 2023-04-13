package com.example.awssqspilot.messaging.message;

import com.example.awssqspilot.messaging.channel.MessageChannel;
import org.springframework.util.Assert;

public interface MessagePublisher<T, R> {

	boolean isSupportChannel(MessageChannel messageChannel);

	MessageSender<T, R> getMessageSender();

	default R send(MessageChannel messageChannel, T message) {

		Assert.notNull(messageChannel, "messageChannel must not be null");
		Assert.notNull(message, "message must not be null");

		if (!isSupportChannel(messageChannel)) {
			throw new IllegalArgumentException();
		}

		return getMessageSender().apply(messageChannel, message);
	}
}
