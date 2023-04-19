package com.example.awssqspilot.springboot.listener;


import com.example.awssqspilot.domain.event.ApplicationEventRepository;
import com.example.awssqspilot.domain.event.EventStatus;
import com.example.awssqspilot.messaging.concrete.sqs.SqsMessage;
import com.example.awssqspilot.messaging.message.MessageHandlerCallback;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RdbOutBoxProtocol<T extends SqsMessage> implements MessageHandlerCallback<SqsMessage, Object> {

	private final ApplicationEventRepository applicationEventRepository;


	@Override public void onStart(final SqsMessage message, final MessageHeaders messageHeaders) {
		final var applicationEvent = applicationEventRepository.findById(message.getEventId())
				.orElseThrow(() -> new EntityNotFoundException("발행되지 않은 EventId 입니다. eventId=" + message.getEventId()));

		if (!EventStatus.PUBLISHED.equals(applicationEvent.getEventStatus())) {
			throw new IllegalArgumentException("이미 처리된 Event입니다. eventId=" + message.getEventId());
		}
	}

	@Override public void onComplete(final SqsMessage message, final MessageHeaders messageHeaders, final Object result) {
		final var applicationEvent = applicationEventRepository.findById(message.getEventId())
				.orElseThrow(() -> new EntityNotFoundException("발행되지 않은 EventId 입니다. eventId=" + message.getEventId()));

		applicationEvent.onComplete();
	}

	@Override public void onError(final SqsMessage message, final MessageHeaders messageHeaders, final Exception e) {

	}
}
