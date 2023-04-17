package com.example.awssqspilot.springboot.listener;

import com.example.awssqspilot.domain.event.EventType;
import com.example.awssqspilot.messaging.model.ApplicationEventMessage;
import com.example.awssqspilot.springboot.messaging.context.EventTypeDispatcher;
import com.example.awssqspilot.springboot.messaging.sqs.SqsMessageHeaders;
import com.example.awssqspilot.util.AcknowledgmentUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class SqsMessageHandler {

	private final AsyncTaskExecutor eventWorkerPool;
	private final EventTypeDispatcher eventTypeDispatcher;
	private final AtomicInteger atomicInteger = new AtomicInteger(0);

	public void handle(
			final ApplicationEventMessage message,
			final MessageHeaders messageHeaders,
			final Acknowledgment acknowledgment,
			final MessageHandlerCallback<ApplicationEventMessage, Object> callback
	) {
		try {
			eventWorkerPool.submit(() -> {

				final Object result;
				try {
					if (callback != null) {
						callback.onStart(message, messageHeaders);
					}

					EventType eventType = getEventType(messageHeaders);

					result = eventTypeDispatcher.doDispatch(eventType, message.getEventPayload());

					AcknowledgmentUtils.ack(acknowledgment);

				} catch (Exception e) {

					log.error("Exception", e);

					if (callback != null) {
						callback.onError(message, messageHeaders, e);
					}
					throw new RuntimeException(e);
				}

				final var andIncrement = atomicInteger.incrementAndGet();
				log.info("procee {}", andIncrement);
				if (callback != null) {
					callback.onComplete(message, messageHeaders, result);
				}

			});
		} catch (RejectedExecutionException e) {
			log.error("eventThreadPool is full. {}", e.getMessage());
		}


	}

	private EventType getEventType(final MessageHeaders messageHeaders) {
		return EventType.from(messageHeaders.get(SqsMessageHeaders.SQS_EVENT_TYPE).toString());
	}


}
