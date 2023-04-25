package com.example.awssqspilot.springboot.listener;

import com.example.awssqspilot.domain.event.ApplicationEventRedisRepository;
import com.example.awssqspilot.domain.event.EventStatus;
import com.marketboro2.advancesqs.messaging.concrete.sqs.SqsMessage;
import com.marketboro2.advancesqs.messaging.message.MessageHandlerCallback;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisOutBoxProtocol<T extends SqsMessage> implements MessageHandlerCallback<SqsMessage, Object> {

	private final ApplicationEventRedisRepository applicationEventRedisRepository;

	@Override public void onStart(final SqsMessage message, final MessageHeaders messageHeaders) {
		final var applicationEvent = applicationEventRedisRepository.findById(message.getEventId())
				.orElseThrow(() -> new EntityNotFoundException("발행되지 않은 EventId 입니다. eventId=" + message.getEventId()));

		if (!EventStatus.PUBLISHED.equals(applicationEvent.getEventStatus())) {
			throw new IllegalArgumentException("이미 처리된 Event입니다. eventId=" + message.getEventId());
		}
	}

	@Override public void onComplete(final SqsMessage message, final MessageHeaders messageHeaders, final Object result) {
		final var applicationEvent = applicationEventRedisRepository.findById(message.getEventId())
				.orElseThrow(() -> new EntityNotFoundException("발행되지 않은 EventId 입니다. eventId=" + message.getEventId()));

		applicationEvent.onComplete();
		applicationEventRedisRepository.update(applicationEvent);
	}

	@Override public void onError(final SqsMessage message, final MessageHeaders messageHeaders, final Exception e) {

	}
}
