package com.marketboro2.advancesqs.messaging.message;


import com.marketboro2.advancesqs.messaging.channel.MessageChannel;

@FunctionalInterface
public interface MessageSender<T, R> {

	R apply(MessageChannel messageChannel, T message);
}
