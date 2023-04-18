package com.example.awssqspilot.domain.service;

import com.example.awssqspilot.domain.event.ApplicationEvent;
import com.example.awssqspilot.domain.event.ApplicationEventRepository;
import com.example.awssqspilot.domain.event.EventSource;
import com.example.awssqspilot.domain.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationEventService {

	private final ApplicationEventRepository applicationEventRepository;

	@Transactional
	public ApplicationEvent recordApplicationEvent(final EventSource event, final String eventPayload) {
		final var applicationEvent = ApplicationEvent.createEvent(
				event.getEventId(),
				event.getEventGroupId(),
				event.getBizGroupNo(),
				event.getBizCd(),
				EventType.REGISTER_MASS_REG,
				eventPayload
		);

		return applicationEventRepository.save(applicationEvent);
	}
}
