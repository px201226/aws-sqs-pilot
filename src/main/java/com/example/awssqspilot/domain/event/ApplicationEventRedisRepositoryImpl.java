package com.example.awssqspilot.domain.event;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApplicationEventRedisRepositoryImpl implements ApplicationEventRedisRepository {

	private final KeyValueTemplate keyValueTemplate;
	@Override public Optional<ApplicationEvent> findById(final String eventId) {
		return keyValueTemplate.findById(eventId, ApplicationEvent.class);
	}

	@Override public ApplicationEvent save(final ApplicationEvent applicationEvent) {
		return keyValueTemplate.insert(applicationEvent);
	}

	@Override public ApplicationEvent update(final ApplicationEvent applicationEvent) {
		return keyValueTemplate.update(applicationEvent);
	}
}
