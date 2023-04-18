package com.example.awssqspilot.domain.event;

import javax.persistence.AttributeConverter;

public class EventStatusConverter implements AttributeConverter<EventStatus, Integer> {

	@Override public Integer convertToDatabaseColumn(final EventStatus attribute) {
		return attribute.getValue();
	}

	@Override public EventStatus convertToEntityAttribute(final Integer dbData) {
		return EventStatus.from(dbData);
	}
}
