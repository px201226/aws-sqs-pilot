package com.example.awssqspilot.domain.listener;


import com.example.awssqspilot.domain.dto.DomainEventModel;
import com.example.awssqspilot.domain.event.EventMessage;
import com.example.awssqspilot.domain.event.EventMessageRepository;
import com.example.awssqspilot.domain.event.EventName;
import com.example.awssqspilot.messaging.channel.MessageChannel;
import com.example.awssqspilot.messaging.message.MessagePublisher;
import com.example.awssqspilot.messaging.message.OrderingEventMessage;
import com.example.awssqspilot.util.UuidUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventListener {

	private final MessagePublisher<OrderingEventMessage, Boolean> sqsMessagePublisher;
	private final EventMessageRepository eventMessageRepository;
	private final ObjectMapper objectMapper;

	/**
	 * 인텔리제이에서 아래 코드 옆에 헤드셋을 누르면 여기로 온다.
	 * applicationEventPublisher.publishEvent(message)
	 * @param o
	 */
//	@EventListener
//	public void mark(Object o){
//
//	}


	@EventListener
	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void sendMessageSqsChannel(DomainEventModel.RegisteredBizSlipTrade event) throws JsonProcessingException {
		log.info("Spring Event received : {}", event);

		final var payload = objectMapper.writeValueAsString(event);

		final var eventMessage = EventMessage.createEvent(
				event.getEventId(),
				event.getMessageGroupId(),
				1L,
				"00001",
				EventName.REGISTER_MASS_REG,
				payload
		);

		eventMessageRepository.save(eventMessage);

		sqsMessagePublisher.send(MessageChannel.MASS_DATA_REG_CHANNEL, event);
	}

}
