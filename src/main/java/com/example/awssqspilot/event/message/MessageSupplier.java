package com.example.awssqspilot.event.message;

@FunctionalInterface
public interface MessageSupplier<T> {

	T apply();
}
