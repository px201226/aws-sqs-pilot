package com.example.awssqspilot.messaging.message;

@FunctionalInterface
public interface MessageSupplier<T> {

	T apply();
}
