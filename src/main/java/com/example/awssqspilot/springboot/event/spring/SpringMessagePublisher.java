package com.example.awssqspilot.springboot.event.spring;

import com.example.awssqspilot.event.channel.MessageChannel;
import com.example.awssqspilot.event.message.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringMessagePublisher<T> implements MessagePublisher<T> {

	private final ApplicationEventPublisher applicationEventPublisher;

	@Override public void send(final MessageChannel channel, final T message) {

		if (!MessageChannel.SPRING_EVENT_CHANNEL.equals(channel)) {
			throw new IllegalArgumentException();
		}

		log.info("channel={}, message={}", channel, message);
		applicationEventPublisher.publishEvent(message);

	}
}
