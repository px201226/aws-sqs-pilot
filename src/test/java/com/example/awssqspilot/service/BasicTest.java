package com.example.awssqspilot.service;

import static org.junit.jupiter.api.Assertions.*;

import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
@SpringBootTest
@DisplayName("SQS FIFO Queue 기본동작 테스트")
class BasicTest extends FifoTest {


	/**
	 * FIFO QUEUE의 경우 MessageGroupId가 필수이다
	 * MessageBuilder setHeader로 헤더를 설정하더라도
	 * QueueMessagingTemplate.convertAndSend 사용 시, 헤더를 따로 전달해줘야한다.
	 * 전달안하고 payLoad만 전달하면 헤더가 설정이 안된다!!
	 */
	@Test
	@DisplayName("메시지 전송 성공 시, exception이 발생하면 안된다")
	void 메시지_전송_성공_시_Exception이_발생하면_안된다() {

		// given
		final var payload = MessageBuilder.withPayload("Hello world")
				.setHeader("message-group-id", "group1")
				.build();

		// when && then
		assertDoesNotThrow(
				() -> queue.convertAndSend(FIFO_QUEUE_NAME, payload, payload.getHeaders())
		);

		// after
		cleanQueue(1);
	}

	/**
	 * DTO 오브젝트를 SQS로 보낼 때, json이 객체로 디코딩되기 위해
	 * Getter, 기본생성자가 있어야 한다.
	 */
	@Test
	@DisplayName("Object형 객체 send하고 receive 받을 수 있다")
	void Object형_객체_send_receive_테스트() {

		// given
		final var expect = new Foo(LocalDateTime.now().toString(), 123);

		// when
		assertDoesNotThrow(
				() -> queue.convertAndSend(FIFO_QUEUE_NAME, expect, HEADERS)
		);

		// then
		final var foo = queue.receiveAndConvert(FIFO_QUEUE_NAME, Foo.class);
		assertEquals(foo, expect);

	}


	@Test
	@DisplayName("FIFO큐에 메시지는 들어온 순서대로 반출되어야 한다")
	void 기본적인_FIFO_동작_테스트() {

		// given
		Queue<String> linkedListQueue = new LinkedList<>(
				List.of("1", "2", "3")
		);

		Queue<String> expectQueue = new LinkedList<>(linkedListQueue);

		// when
		while (!linkedListQueue.isEmpty()) {
			final var payload = MessageBuilder.withPayload(linkedListQueue.poll())
					.setHeader("message-group-id", "group1")
					.build();

			queue.convertAndSend(FIFO_QUEUE_NAME, payload, payload.getHeaders());
		}

		// then
		while (!expectQueue.isEmpty()) {
			final Map<String, String> payload = queue.receiveAndConvert(FIFO_QUEUE_NAME, Map.class);
			assertEquals(expectQueue.poll(), payload.get("payload"));
		}
	}


	@Test
	@DisplayName("수신만 된 메시지는 visibilty time 동안 다른 컨슈머에게 노출되지 않는다")
	void 수신만_된_메시지는_visibilty_time_동안_다른_컨슈머에게_노출되지_않는다() {

		// given
		final var expect = new Foo(LocalDateTime.now().toString(), 123);
		queue.convertAndSend(FIFO_QUEUE_NAME, expect, HEADERS);

		// when
		final var receiveMessageResult = amazonSQS.receiveMessage(
				new ReceiveMessageRequest(FIFO_QUEUE_URL).withAttributeNames("All").withWaitTimeSeconds(20)
		);

		// then
		assertNull(queue.receive(FIFO_QUEUE_NAME));

		// after
		amazonSQS.changeMessageVisibility(
				new ChangeMessageVisibilityRequest(
						FIFO_QUEUE_URL,
						receiveMessageResult.getMessages().get(0).getReceiptHandle(),
						0
				)
		);
		queue.receiveAndConvert(FIFO_QUEUE_NAME, Foo.class);
	}

	@Test
	@DisplayName("수신만 된 메시지가 visibility timeout 이후에는 다시 대기열에서 deque할 수 있다")
	void 수신만_된_메시지가_visibility_timeout_이후에는_다시_대기열에서_deque할_수_있다() {
		// given
		final var expect = new Foo(LocalDateTime.now().toString(), 123);
		queue.convertAndSend(FIFO_QUEUE_NAME, expect, HEADERS);
		final var receiveMessageResult = amazonSQS.receiveMessage(FIFO_QUEUE_URL);

		// when
		// 메시지 가시성 타임을 0으로 설정
		amazonSQS.changeMessageVisibility(
				new ChangeMessageVisibilityRequest(
						FIFO_QUEUE_URL,
						receiveMessageResult.getMessages().get(0).getReceiptHandle(),
						0
				)
		);
		final var foo = queue.receiveAndConvert(FIFO_QUEUE_NAME, Foo.class);

		// then
		assertEquals(expect, foo);
	}

	@Test
	@DisplayName("여러 컨슈머가 메시지를 poll 하더라도 같은 MessageGroupId내에서 순서가 보장된다")
	void 여러_컨슈머가_메시지를_poll_하더라도_같은_MessageGroupId내에서_순서가_보장된다() throws InterruptedException {

		// given
		final Queue<Integer> linkedListQueue = new ConcurrentLinkedQueue<>();
		final var randomMessageGroupIdHeader = getRandomMessageGroupIdHeader();
		final var endInclusive = 100;
		IntStream.rangeClosed(1, endInclusive)
				.forEach(i -> {
							linkedListQueue.add(i);
							queue.convertAndSend(FIFO_QUEUE_NAME, i, randomMessageGroupIdHeader);
							log.info("publish {}", i);
						}
				);

		final Integer consumerNumber = 10;

		// when
		final var executorService = Executors.newFixedThreadPool(consumerNumber);
		final var countDownLatch = new CountDownLatch(endInclusive);
		for (int i = 0; i < consumerNumber; i++) {
			executorService.execute(() -> {
						while (countDownLatch.getCount() != 0) {

							final var integer = queue.receiveAndConvert(FIFO_QUEUE_NAME, Integer.class);
							if (integer == null) {
								log.info("null");
								continue;
							}

							log.info("consumer : {}", integer);
							countDownLatch.countDown();
							final var poll = linkedListQueue.poll();
							if (!poll.equals(integer)) {
								log.error("expect = {}, actual = {}", poll, integer);
							}
						}
					}
			);
		}

		countDownLatch.await();
	}

	@Test
	@DisplayName("대기열에 여러 MessageGroupId가 섞여져 있고, Cunsuming하는 경우 순서가 보장되는지 검증")
	void test1() {

		// given
		final var 대기열_수 = 10;
		final var 대기열당_메시지_수 = 10;
		final Queue<Foo> concurrentQueue = new ConcurrentLinkedQueue<>();

		for (int i = 1; i <= 대기열_수; i++) {
			for (int j = 1; j <= 대기열당_메시지_수; j++) {
				final var foo = new Foo(String.valueOf(i), j);
				concurrentQueue.add(foo);
				queue.send(FIFO_QUEUE_NAME,
						MessageBuilder.withPayload(foo.value1)
								.setHeader("message-group-id", foo.value2.toString())
								.setHeader("message-deduplication-id", java.time.LocalDateTime.now().toString())
								.build()
				);
			}
		}

		Integer count = 대기열_수 * 대기열당_메시지_수;
		while (count > 0) {
			final Message<?> receive = queue.receive(FIFO_QUEUE_NAME);
			if (receive == null) {
				continue;
			}

			log.info("message_group_id={}, payload={}", receive.getHeaders().get("MessageGroupId"), receive.getPayload());
			count--;
			final var poll = concurrentQueue.poll();
			Assertions.assertEquals(poll.value1.toString(), receive.getPayload().toString());
			Assertions.assertEquals(poll.value2.toString(), receive.getHeaders().get("MessageGroupId"));
		}
	}


}