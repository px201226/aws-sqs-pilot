package com.example.awssqspilot.springboot.messaging.spring;

import static com.example.awssqspilot.messaging.channel.MessageChannel.SPRING_EVENT_CHANNEL;

import com.example.awssqspilot.messaging.channel.MessageChannel;
import com.example.awssqspilot.messaging.message.MessagePublisher;
import com.example.awssqspilot.messaging.message.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.RequestHandledEvent;

@Slf4j
@Component()
@RequiredArgsConstructor
public class SpringMessagePublisher<T> implements MessagePublisher<T, Boolean> {

	private final ApplicationEventPublisher applicationEventPublisher;

	@Override public boolean isSupportChannel(final MessageChannel messageChannel) {
		return SPRING_EVENT_CHANNEL.equals(messageChannel);
	}

	@EventListener
	public void eventHandleMethod(RequestHandledEvent event) {
		//HTTP 요청을 처리했을 때 발생하는 이벤트
		System.out.println("처리완료");
	}

	@Override public MessageSender<T, Boolean> getMessageSender() {
		return (channel, message) -> {
			log.info("channel={}, message={}", channel, message);
			applicationEventPublisher.publishEvent(message);
			return true;
		};
	}

}
