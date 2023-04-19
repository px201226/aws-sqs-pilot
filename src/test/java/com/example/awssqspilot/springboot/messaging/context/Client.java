package com.example.awssqspilot.springboot.messaging.context;


import com.example.awssqspilot.messaging.channel.MessageChannel;
import com.example.awssqspilot.messaging.concrete.sqs.SqsMessageHeaders;
import com.example.awssqspilot.messaging.concrete.sqs.SqsMessagePublisher;
import com.example.awssqspilot.util.UuidUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootTest
public class Client {

	public static CountDownLatch countDownLatch = new CountDownLatch(2);

	@Autowired SqsMessagePublisher sqsMessagePublisher;

	@Test
	void test() throws InterruptedException {

		final var sqsMessageModel1 = new SqsMessageModel("hello world1", UuidUtils.generateUuid());
		final Map<String, Object> header1 = new HashMap<>() {{
			put(SqsMessageHeaders.SQS_GROUP_ID_HEADER, UuidUtils.generateUuid());
			put(SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER, "1");
			put(SqsMessageHeaders.SQS_EVENT_TYPE, "TEST1");
		}};

		final var message1 = MessageBuilder.createMessage(sqsMessageModel1, new MessageHeaders(header1));

		sqsMessagePublisher.send(MessageChannel.MASS_DATA_REG_CHANNEL, message1);

		/////////////////////////////////////////////////////////////////////////////

		final var sqsMessageModel2 = new SqsMessageModel("hello world2", UuidUtils.generateUuid());
		final Map<String, Object> header2 = new HashMap<>() {{
			put(SqsMessageHeaders.SQS_GROUP_ID_HEADER, UuidUtils.generateUuid());
			put(SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER, "1");
			put(SqsMessageHeaders.SQS_EVENT_TYPE, "TEST2");
		}};

		final var message2 = MessageBuilder.createMessage(sqsMessageModel2, new MessageHeaders(header2));

		sqsMessagePublisher.send(MessageChannel.MASS_DATA_REG_CHANNEL, message2);


		///////////////////////////////
		countDownLatch.await();
	}

}
