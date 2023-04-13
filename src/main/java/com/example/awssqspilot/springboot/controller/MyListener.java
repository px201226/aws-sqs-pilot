package com.example.awssqspilot.springboot.controller;


import com.example.awssqspilot.util.AcknowledgmentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Profile("test")
@Slf4j
@Component
public class MyListener {

	@SqsListener(value = RestController.FIFO_QUEUE_NAME, deletionPolicy = SqsMessageDeletionPolicy.NEVER)
	public void listen(@Payload Foo foo, Acknowledgment acknowledgment) throws NoSuchFieldException, IllegalAccessException {
		log.info("listen payload {}", foo);
		log.info("listen payload {}", foo);

		AcknowledgmentUtils.ack(acknowledgment);

	}

}
