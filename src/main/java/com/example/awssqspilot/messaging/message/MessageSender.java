package com.example.awssqspilot.messaging.message;

import com.example.awssqspilot.messaging.channel.MessageChannel;

@FunctionalInterface
public interface MessageSender<T, R> {

	R apply(MessageChannel messageChannel, T message);
}
