package com.example.awssqspilot.springboot.messaging.context;

import com.example.awssqspilot.messaging.annotation.MessageTypeMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainListener {

	@MessageTypeMapping(messageType = "TEST1")
	public void processMessage1(String model) {
		Client.countDownLatch.countDown();
		log.info("process Message1 {}", model);
	}

	@MessageTypeMapping(messageType = "TEST2")
	public void processMessage2(String model) {
		Client.countDownLatch.countDown();
		log.info("process Message2 {}", model);
	}


}
