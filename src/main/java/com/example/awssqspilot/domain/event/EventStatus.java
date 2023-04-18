package com.example.awssqspilot.domain.event;


import java.util.Arrays;
import lombok.Getter;

@Getter
public enum EventStatus {

	PUBLISHED(0),
	COMPLETED(1),
	FAILED(2);


	private final Integer value;

	EventStatus(final Integer value) {
		this.value = value;
	}

	public static EventStatus from(Integer value) {
		return Arrays.stream(EventStatus.values())
				.filter(e -> e.getValue() == value)
				.findFirst()
				.orElse(null);
	}
}
