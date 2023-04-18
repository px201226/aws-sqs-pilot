package com.example.awssqspilot.springboot.listener;

import com.example.awssqspilot.domain.event.EventType;
import com.example.awssqspilot.domain.model.ApplicationEventMessage;
import com.example.awssqspilot.messaging.context.EventTypeDispatcher;
import com.example.awssqspilot.messaging.concrete.sqs.SqsMessageHeaders;
import com.example.awssqspilot.util.AcknowledgmentUtils;
import java.util.concurrent.RejectedExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;


@Slf4j
@Component
@RequiredArgsConstructor
public class SqsMessageHandler {

	private final TransactionTemplate transactionTemplate;
	private final AsyncTaskExecutor eventWorkerPool;
	private final EventTypeDispatcher eventTypeDispatcher;
	@Value("${cloud.aws.sqs.backOffTime}")
	private Long backOffTime;

	public void handle(
			final ApplicationEventMessage message,
			final MessageHeaders messageHeaders,
			final Acknowledgment acknowledgment,
			final MessageHandlerCallback<ApplicationEventMessage, Object> callback
	) {
		try {
			eventWorkerPool.submit(() -> {

				try {

					transactionTemplate.execute(status ->
							doTransaction(message, messageHeaders, acknowledgment, callback)
					);

				} catch (Exception e) {

					log.error("EventWorkerPool Exception occurred", e);

					if (callback != null) {
						callback.onError(message, messageHeaders, e);
					}

					try {
						// noinspection BusyWait
						Thread.sleep(backOffTime);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}

					throw new RuntimeException(e);
				}


			});

		} catch (RejectedExecutionException e) {
			log.error("eventThreadPool Queue is full. {}", e.getMessage());
			throw e;
		}


	}

	private Object doTransaction(final ApplicationEventMessage message, final MessageHeaders messageHeaders, final Acknowledgment acknowledgment,
			final MessageHandlerCallback<ApplicationEventMessage, Object> callback) {

		if (callback != null) {
			callback.onStart(message, messageHeaders);
		}

		EventType eventType = getEventType(messageHeaders);

		final Object result = eventTypeDispatcher.doDispatch(eventType, message.getEventPayload());

		AcknowledgmentUtils.ack(acknowledgment);

		if (callback != null) {
			callback.onComplete(message, messageHeaders, result);
		}

		return result;
	}

	private EventType getEventType(final MessageHeaders messageHeaders) {
		return EventType.from(messageHeaders.get(SqsMessageHeaders.SQS_EVENT_TYPE).toString());
	}


}
