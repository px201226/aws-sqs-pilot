package com.example.awssqspilot.domain.event;

import java.util.Arrays;

public enum EventType {

	REGISTER_MASS_REG;


	public static EventType from(String value){
		return Arrays.stream(EventType.values())
				.filter(e -> e.name().equals(value))
				.findFirst()
				.orElse(null);
	}
}
