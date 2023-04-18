package com.example.awssqspilot.domain.model;

import com.example.awssqspilot.domain.event.ApplicationEvent;
import com.example.awssqspilot.domain.event.EventStatus;
import com.example.awssqspilot.domain.event.EventType;
import com.example.awssqspilot.messaging.concrete.sqs.SqsMessage;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ApplicationEventMessage implements SqsMessage {

	private String eventId;

	private String eventGroupId;

	private Long bizGroupNo;

	private String bizCd;

	private EventStatus eventStatus;

	private EventType eventType;

	private String eventPayload;

	private Long regNo = 0L;

	private LocalDateTime regDt;

	private LocalDateTime updDt;

	@Override public String getPayload() {
		return eventPayload;
	}

	public static ApplicationEventMessage from(ApplicationEvent applicationEvent) {
		return ApplicationEventMessage.builder()
				.eventId(applicationEvent.getEventId())
				.eventGroupId(applicationEvent.getEventGroupId())
				.bizGroupNo(applicationEvent.getBizGroupNo())
				.bizCd(applicationEvent.getBizCd())
				.eventStatus(applicationEvent.getEventStatus())
				.eventType(applicationEvent.getEventType())
				.eventPayload(applicationEvent.getEventPayload())
				.regNo(applicationEvent.getRegNo())
				.regDt(applicationEvent.getRegDt())
				.updDt(applicationEvent.getUpdDt())
				.build();
	}
}
