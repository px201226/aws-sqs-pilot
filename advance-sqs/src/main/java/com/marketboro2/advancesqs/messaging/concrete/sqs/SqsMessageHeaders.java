package com.marketboro2.advancesqs.messaging.concrete.sqs;

import java.util.Map;

public class SqsMessageHeaders extends org.springframework.cloud.aws.messaging.core.SqsMessageHeaders {

	public static final String SQS_EVENT_TYPE = "event-type";
	public SqsMessageHeaders(final Map<String, Object> headers) {
		super(headers);
	}
}
