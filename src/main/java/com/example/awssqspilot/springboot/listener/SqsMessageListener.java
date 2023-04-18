package com.example.awssqspilot.springboot.listener;


import com.example.awssqspilot.domain.model.ApplicationEventMessage;
import com.example.awssqspilot.messaging.concrete.sqs.SqsChannelResolver;
import com.example.awssqspilot.messaging.concrete.sqs.SqsMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

//@Profile("test")
@Slf4j
@Component
@RequiredArgsConstructor
public class SqsMessageListener {

	private final SqsMessageHandler sqsMessageHandler;
	private final OutBoxProtocol outBoxProtocol;
	@SqsListener(value = SqsChannelResolver.FIFO_QUEUE_NAME, deletionPolicy = SqsMessageDeletionPolicy.NEVER)
	public void handle(@Payload ApplicationEventMessage message, MessageHeaders messageHeaders, Acknowledgment acknowledgment) throws InterruptedException {
		sqsMessageHandler.handle(message, messageHeaders, acknowledgment, outBoxProtocol);
	}

}
