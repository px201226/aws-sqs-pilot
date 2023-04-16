package com.example.awssqspilot.domain.listener;


import com.example.awssqspilot.domain.dto.DomainEventModel;
import com.example.awssqspilot.domain.event.EventType;
import com.example.awssqspilot.domain.service.ApplicationEventService;
import com.example.awssqspilot.messaging.channel.MessageChannel;
import com.example.awssqspilot.messaging.message.MessagePublisher;
import com.example.awssqspilot.messaging.model.ApplicationEventMessage;
import com.example.awssqspilot.springboot.messaging.annotation.EventTypeMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventListener {

	private final MessagePublisher<Message, Boolean> sqsMessagePublisher;
	private final ApplicationEventService applicationEventService;
	private final ObjectMapper objectMapper;

	/**
	 * 인텔리제이에서 아래 코드 옆에 헤드셋을 누르면 여기로 온다.
	 * applicationEventPublisher.publishEvent(message)
	 */
	@EventListener
	public void mark(DomainEventModel.RegisteredBizSlipTrade event) {
		System.out.println("d");
	}


	@EventListener
	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void sqsPublishListener(DomainEventModel.RegisteredBizSlipTrade event) throws JsonProcessingException {
		log.info("Spring Event received : {}", event);

		final var applicationEvent = applicationEventService.recordApplicationEvent(event, objectMapper.writeValueAsString(event));
		final var applicationEventMessage = ApplicationEventMessage.from(applicationEvent);

		sqsMessagePublisher.send(
				MessageChannel.MASS_DATA_REG_CHANNEL,
				MessageBuilder.createMessage(applicationEventMessage, new MessageHeaders(getSqsMessageHeader(event)))
		);

	}

	private HashMap<String, Object> getSqsMessageHeader(final DomainEventModel.RegisteredBizSlipTrade event) {
		return new HashMap<>() {{
			put(SqsMessageHeaders.SQS_GROUP_ID_HEADER, event.getMessageGroupId());
			put(SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER, event.getDeduplicationId());
		}};
	}

	@EventTypeMapping(eventType = EventType.REGISTER_MASS_REG)
	public void aa(){

	}


}
