package com.example.awssqspilot.messaging.concrete.sqs;

import com.example.awssqspilot.messaging.context.MessageTypeDispatcher;
import com.example.awssqspilot.messaging.message.MessageHandlerCallback;
import com.example.awssqspilot.messaging.message.MessageType;
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
	private final MessageTypeDispatcher messageTypeDispatcher;
	@Value("${cloud.aws.sqs.backOffTime}")
	private Long backOffTime;

	public void handle(
			final SqsMessage message,
			final MessageHeaders messageHeaders,
			final Acknowledgment acknowledgment,
			final MessageHandlerCallback<SqsMessage, Object> callback
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

	private Object doTransaction(final SqsMessage message, final MessageHeaders messageHeaders, final Acknowledgment acknowledgment,
			final MessageHandlerCallback<SqsMessage, Object> callback) {

		if (callback != null) {
			callback.onStart(message, messageHeaders);
		}

		final Object result = messageTypeDispatcher.doDispatch(getMessageType(messageHeaders), message.getPayload());

		AcknowledgmentUtils.ack(acknowledgment);

		if (callback != null) {
			callback.onComplete(message, messageHeaders, result);
		}

		return result;
	}

	private MessageType getMessageType(final MessageHeaders messageHeaders) {
		return new MessageType(messageHeaders.get(SqsMessageHeaders.SQS_EVENT_TYPE).toString());
	}


}
