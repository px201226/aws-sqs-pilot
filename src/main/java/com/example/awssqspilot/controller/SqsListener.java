package com.example.awssqspilot.controller;


import com.example.awssqspilot.util.AcknowledgmentUtils;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.QueueMessageAcknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Profile("dev")
@Slf4j
@Component
public class SqsListener {

	@org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
			(value = RestController.FIFO_QUEUE_NAME,
					deletionPolicy = SqsMessageDeletionPolicy.NEVER
			)
	public void listen(@Payload Foo foo, Acknowledgment acknowledgment) throws NoSuchFieldException, IllegalAccessException {
		log.info("listen payload {}", foo);
		log.info("listen payload {}", foo);

		AcknowledgmentUtils.ack(acknowledgment);

	}

	@org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
			(value = RestController.FIFO_QUEUE_NAME,
					deletionPolicy = SqsMessageDeletionPolicy.NEVER
			)
	public void listen2(@Payload Bar bar, Acknowledgment acknowledgment) throws NoSuchFieldException, IllegalAccessException {
		log.info("listen payload {}", bar);
		log.info("listen payload {}", bar);

//		Class<QueueMessageAcknowledgment> fooClass = QueueMessageAcknowledgment.class;
//
//		Field varField = fooClass.getDeclaredField("receiptHandle");
//		varField.setAccessible(true);
//		varField.set(acknowledgment, "abc");

		final Future<?> acknowledge = acknowledgment.acknowledge();
		try {
			final Object o = acknowledge.get();
			log.info("d");
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
