package com.example.awssqspilot.domain.event;

import static org.junit.jupiter.api.Assertions.*;

import com.example.awssqspilot.util.UuidUtils;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationEventRedisRepositoryTest {

	@Autowired ApplicationEventRedisRepository applicationEventRedisRepository;

	@Test
	void ads(){
		final var abc = ApplicationEvent.createEvent(
				UuidUtils.generateUuid(),
				UuidUtils.generateUuid(),
				1L,
				"00001",
				EventType.REGISTER_MASS_REG,
				"abc"
		);

		final var save = applicationEventRedisRepository.save(abc);

		final var byId = applicationEventRedisRepository.findById("20a3fe9c5f38699b75280da5dea609144a24da6b630e6ab009164d3ffafe3fa8");

		System.out.println("d");

	}
}