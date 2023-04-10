package com.example.awssqspilot.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;

public class FifoTest {

	@Autowired
	public QueueMessagingTemplate queue;

	@Autowired
	public AmazonSQS amazonSQS;

	public StringMessageConverter stringMessageConverter = new StringMessageConverter(Charset.defaultCharset());
	public MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();

	public final Map<String, Object> HEADERS = new HashMap<String, Object>() {{
		put("message-group-id", "group5");
	}};

	public final String FIFO_QUEUE_NAME = "async_ff.fifo";
	public final String FIFO_QUEUE_URL = "https://sqs.ap-northeast-2.amazonaws.com/832626921517/async_ff.fifo";


	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	public static class Foo {

		String value1;
		Integer value2;
	}


	public void cleanQueue(final Integer count) {
		for (int i = 0; i < count; i++) {
			queue.receive(FIFO_QUEUE_NAME);

		}
	}

	public Map<String, Object> getRandomMessageGroupIdHeader() {
		final Map<String, Object> headers = new HashMap<String, Object>() {{
			put("message-group-id", LocalDateTime.now().toString());
		}};

		return headers;
	}

	public <T> T polling(Supplier<T> polling) {
		while (true) {
			final var value = polling.get();
			if (value instanceof ReceiveMessageResult) {
				if (((ReceiveMessageResult) value).getMessages().size() != 0) {
					return value;
				}
			} else {
				if (Objects.nonNull(value)) {
					return value;
				}
			}
		}
	}


}
