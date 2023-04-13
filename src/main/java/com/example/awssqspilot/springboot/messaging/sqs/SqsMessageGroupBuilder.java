package com.example.awssqspilot.springboot.messaging.sqs;

import static org.springframework.cloud.aws.messaging.core.SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER;

import com.example.awssqspilot.messaging.message.MessageSupplier;
import java.util.Map;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

public class SqsMessageGroupBuilder<T> {

	private T payload;
	private final Map<String, Object> headers;

	public SqsMessageGroupBuilder(final T payload, final Map<String, Object> headers) {
		this.payload = payload;
		this.headers = headers;
	}

	public SqsMessageGroupBuilder<T> setPayload(final T payload) {
		this.payload = payload;
		return this;
	}

	public SqsMessageGroupBuilder<T> setEventId(final String eventId) {
		this.headers.put("EVENT_ID", eventId);
		return this;
	}

	public MessageSupplier<Message<T>> get() {
		return () -> {
			final var message = MessageBuilder.createMessage(payload, new MessageHeaders(headers));
			final var nextDeduplicationId = Integer.parseInt(headers.getOrDefault(SQS_DEDUPLICATION_ID_HEADER, "1").toString()) + 1;
			this.headers.put(SQS_DEDUPLICATION_ID_HEADER, String.valueOf(nextDeduplicationId));
			return message;
		};
	}
}
