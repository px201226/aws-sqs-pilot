package com.example.awssqspilot.event.message;

import com.example.awssqspilot.event.channel.MessageChannel;

public interface MessagePublisher<T> {

	void send(MessageChannel channel, T message);
}
