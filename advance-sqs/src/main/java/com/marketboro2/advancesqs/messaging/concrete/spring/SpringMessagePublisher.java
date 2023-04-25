package com.marketboro2.advancesqs.messaging.concrete.spring;

import static com.marketboro2.advancesqs.messaging.channel.MessageChannel.SPRING_EVENT_CHANNEL;

import com.marketboro2.advancesqs.messaging.channel.MessageChannel;
import com.marketboro2.advancesqs.messaging.message.MessagePublisher;
import com.marketboro2.advancesqs.messaging.message.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
@RequiredArgsConstructor
public class SpringMessagePublisher<T> implements MessagePublisher<T, Boolean> {

	private final ApplicationEventPublisher applicationEventPublisher;

	@Override public boolean isSupportChannel(final MessageChannel messageChannel) {
		return SPRING_EVENT_CHANNEL.equals(messageChannel);
	}

	@Override public MessageSender<T, Boolean> getMessageSender() {
		return (channel, message) -> {
			log.info("channel={}, message={}", channel, message);
			applicationEventPublisher.publishEvent(message);
			return true;
		};
	}

}
