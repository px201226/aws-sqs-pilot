package com.example.awssqspilot.springboot.listener;


import com.example.awssqspilot.messaging.model.ApplicationEventMessage;
import com.example.awssqspilot.springboot.messaging.sqs.SqsChannelResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsMessageListener {

	private final SqsMessageHandler sqsMessageHandler;

	@SqsListener(value = SqsChannelResolver.FIFO_QUEUE_NAME, deletionPolicy = SqsMessageDeletionPolicy.NEVER)
	public void handle(@Payload ApplicationEventMessage message, MessageHeaders messageHeaders, Acknowledgment acknowledgment) {
		log.info("received message : {}", message);
		sqsMessageHandler.handle(message, messageHeaders, acknowledgment,
				() -> {
					log.info("progress id={}", messageHeaders.get("MessageDeduplicationId"));
					Thread.sleep(10000L);
					log.info("complete id={}", messageHeaders.get("MessageDeduplicationId"));
					return true;
				},
				null
		);
	}

}
