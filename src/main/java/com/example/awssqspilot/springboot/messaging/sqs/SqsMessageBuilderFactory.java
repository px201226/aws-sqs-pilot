package com.example.awssqspilot.springboot.messaging.sqs;

import static org.springframework.cloud.aws.messaging.core.SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER;
import static org.springframework.cloud.aws.messaging.core.SqsMessageHeaders.SQS_GROUP_ID_HEADER;

import com.example.awssqspilot.util.UuidUtils;
import java.util.HashMap;
import java.util.Map;


public class SqsMessageBuilderFactory<T> {

	private T payload;
	private final Map<String, Object> headers;

	private SqsMessageBuilderFactory(final T payload, final Map<String, Object> headers) {
		this.payload = payload;
		this.headers = headers;
	}

	public static SqsMessageGroupBuilder messageGroupBuilder() {
		String uuid = null;
		try {
			uuid = UuidUtils.generateUuid();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return messageGroupBuilder(uuid);
	}

	public static SqsMessageGroupBuilder messageGroupBuilder(final String messageGroupId) {
		final var initHeaders = new HashMap<>() {{
			put(SQS_GROUP_ID_HEADER, messageGroupId);
			put(SQS_DEDUPLICATION_ID_HEADER, "1");
		}};

		return new SqsMessageGroupBuilder(null, initHeaders);
	}


}
