package com.example.awssqspilot.springboot.controller;


import com.example.awssqspilot.domain.dto.DomainEventModel.RegisteredBizSlipTrade;
import com.example.awssqspilot.util.UuidUtils;
import com.marketboro2.advancesqs.messaging.channel.MessageChannel;
import com.marketboro2.advancesqs.messaging.message.MessagePublisher;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Slf4j
@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
public class RestController {

	private final MessagePublisher springMessagePublisher;
	public static final String FIFO_QUEUE_NAME = "async_ff.fifo";

	@PostMapping("/api/post/foo/{num}")
	@Transactional
	public void send(@PathVariable("num") Integer num) {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for (int i = 0; i < num; i++) {
			final var registeredBizSlipTrade = new RegisteredBizSlipTrade
					(1L, "00001", LocalDateTime.now(), UuidUtils.generateUuid(), UuidUtils.generateUuid(), String.valueOf(i));
			springMessagePublisher.send(MessageChannel.SPRING_EVENT_CHANNEL, registeredBizSlipTrade);
		}
		stopWatch.stop();
		log.info("끝============={}", stopWatch.getTotalTimeMillis());
	}


}
