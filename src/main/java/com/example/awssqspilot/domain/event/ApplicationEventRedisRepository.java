package com.example.awssqspilot.domain.event;


import java.util.Optional;

public interface ApplicationEventRedisRepository {

	Optional<ApplicationEvent> findById(String eventId);

	ApplicationEvent save(ApplicationEvent applicationEvent);

	ApplicationEvent update(ApplicationEvent applicationEvent);
}
