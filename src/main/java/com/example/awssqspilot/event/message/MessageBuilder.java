package com.example.awssqspilot.event.message;

public class MessageBuilder {

	public static <T> T build(MessageSupplier<T> messageSupplier) {
		return messageSupplier.apply();
	}

}
