package com.example.awssqspilot.springboot.listener;

import org.springframework.messaging.MessageHeaders;

public interface MessageHandlerCallback<T, R> {

	void onStart(T message, MessageHeaders messageHeaders);

	void onComplete(T message, MessageHeaders messageHeaders, R result);

	void onError(T message, MessageHeaders messageHeaders, Exception e);

}
