package com.example.awssqspilot.service;

import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StopWatch;

@Slf4j
@SpringBootTest
@DisplayName("SQS FIFO Queue 시나리오 테스트")
public class AdvanceTest extends FifoTest {

	@Test
	@DisplayName("같은 MessageGroupId를 가지는 메시지 시퀀셜에서 선두 메시지가 처리되지 않으면 후발 메시지가 수신되지 않는다(블록킹된다) ")
	void test11() {

		// given
		final var messageGroupId = LocalDateTime.now().toString();

		queue.send(FIFO_QUEUE_NAME,
				MessageBuilder.withPayload(1)
						.setHeader(SqsMessageHeaders.SQS_GROUP_ID_HEADER, messageGroupId)
						.build()
		);
		queue.send(FIFO_QUEUE_NAME,
				MessageBuilder.withPayload(2)
						.setHeader(SqsMessageHeaders.SQS_GROUP_ID_HEADER, messageGroupId)
						.build()
		);
		final var stopWatch = new StopWatch();

		stopWatch.start();
		// when
		final var receiveMessageResult1 = polling(
				() -> amazonSQS.receiveMessage(
						new ReceiveMessageRequest(FIFO_QUEUE_URL).withAttributeNames("All").withWaitTimeSeconds(20)
				)
		);
		stopWatch.stop();
		log.info("polling 걸린시간 : {}", stopWatch.getTotalTimeSeconds());

		stopWatch.start();
		final var receiveMessageResult2 = amazonSQS.receiveMessage(
				new ReceiveMessageRequest(FIFO_QUEUE_URL).withAttributeNames("All").withWaitTimeSeconds(5)
		);
		stopWatch.stop();
		log.info("polling 걸린시간 : {}", stopWatch.getTotalTimeSeconds());

		// then
		final var body = receiveMessageResult1.getMessages().get(0).getBody();
		final var sqsMessageGroupId = receiveMessageResult1.getMessages().get(0).getAttributes().get("MessageGroupId");
		Assertions.assertEquals(String.valueOf(1), body);
		Assertions.assertEquals(messageGroupId.toString(), sqsMessageGroupId);
		Assertions.assertEquals(0, receiveMessageResult2.getMessages().size());

		// after
		amazonSQS.changeMessageVisibility(
				new ChangeMessageVisibilityRequest(
						FIFO_QUEUE_URL,
						receiveMessageResult1.getMessages().get(0).getReceiptHandle(),
						0
				)
		);
		cleanQueue(2);
	}

	@Test
	@DisplayName("Visibility Time이 지난 메시지를 deleteMessage 하면 예외가 발생한다")
	void Visibility_Time이_지난_메시지를_deleteMessage_하면_예외가_발생한다() throws InterruptedException {

		// given
		queue.send(FIFO_QUEUE_NAME,
				MessageBuilder.withPayload(1)
						.setHeader(SqsMessageHeaders.SQS_GROUP_ID_HEADER, LocalDateTime.now().toString())
						.build()
		);

		log.info("send message");

		final var receiveMessageResult = polling(() -> amazonSQS.receiveMessage(
				new ReceiveMessageRequest(FIFO_QUEUE_URL).withAttributeNames("All").withWaitTimeSeconds(20)
		));

		amazonSQS.changeMessageVisibility(
				new ChangeMessageVisibilityRequest(
						FIFO_QUEUE_URL,
						receiveMessageResult.getMessages().get(0).getReceiptHandle(),
						3
				)
		);

		log.info("receive and Sleep 10s");
		Thread.sleep(10000);

		// when && then
		Assertions.assertThrows(AmazonSQSException.class, () ->
				amazonSQS.deleteMessage(FIFO_QUEUE_URL, receiveMessageResult.getMessages().get(0).getReceiptHandle())
		);


	}

}
