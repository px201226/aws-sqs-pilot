package com.example.awssqspilot.messaging.message;

import org.springframework.util.Assert;

public class MessageBuilder {

	public static <T> T build(MessageSupplier<T> messageSupplier) {

		Assert.notNull(messageSupplier, "messageSupplier must not be null");

		return messageSupplier.apply();
	}

}
