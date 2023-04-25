package com.marketboro2.advancesqs.messaging.message;

@FunctionalInterface
public interface MessageSupplier<T> {

	T apply();
}
