package com.example.awssqspilot.springboot.messaging.sqs;

import com.example.awssqspilot.messaging.message.OrderingEventMessage;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class SqsMessageBuilder {

	public static Message<OrderingEventMessage> from(OrderingEventMessage orderingEventMessage) {

		return MessageBuilder.withPayload(orderingEventMessage)
				.setHeader(SqsMessageHeaders.SQS_GROUP_ID_HEADER, orderingEventMessage.getMessageGroupId())
				.setHeader(SqsMessageHeaders.SQS_GROUP_ID_HEADER, orderingEventMessage.getMessageGroupId())
				.build();

	}
}
