package com.example.awssqspilot.springboot.messaging.context;

import com.example.awssqspilot.domain.model.ApplicationEventMessage;
import com.example.awssqspilot.messaging.concrete.sqs.SqsMessage;
import com.example.awssqspilot.messaging.concrete.sqs.SqsMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MySqsListener {

	private final SqsMessageHandler sqsMessageHandler;

	@SqsListener(value = "async_ff.fifo", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
	public void handle(@Payload SqsMessageModel message, MessageHeaders messageHeaders, Acknowledgment acknowledgment) {
		sqsMessageHandler.handle(message, messageHeaders, acknowledgment, null);
	}

}
