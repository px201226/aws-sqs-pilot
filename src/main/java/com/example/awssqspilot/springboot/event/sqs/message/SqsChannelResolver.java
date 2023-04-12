package com.example.awssqspilot.springboot.event.sqs.message;

import com.example.awssqspilot.event.channel.MessageChannel;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SqsChannelResolver {

	public static final String FIFO_QUEUE_NAME = "async_ff.fifo";
	private final Set<MessageChannel> supportChannels = Set.of(
			MessageChannel.MASS_DATA_REG_CHANNEL
	);

	public String resolve(MessageChannel messageChannel) {
		if (!supportChannels.contains(messageChannel)) {
			throw new IllegalArgumentException();
		}

		switch (messageChannel) {
			case MASS_DATA_REG_CHANNEL:
				return FIFO_QUEUE_NAME;
			default:
				throw new IllegalArgumentException();
		}
	}
}
