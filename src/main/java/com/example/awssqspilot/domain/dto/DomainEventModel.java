package com.example.awssqspilot.domain.dto;

import com.example.awssqspilot.messaging.message.OrderingEventMessage;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

public class DomainEventModel {


	@Data
	@AllArgsConstructor
	public static class RegisteredBizSlipTrade implements OrderingEventMessage {

		private Long bizGroupNo;
		private String bizCd;
		private LocalDateTime regDt;
		private String eventId;
		private String messageGroupId;
		private String deduplicationId;

		@Override public String getEventId() {
			return eventId;
		}

		@Override public String getMessageGroupId() {
			return messageGroupId;
		}

		@Override public String getDeduplicationId() {
			return deduplicationId;
		}
	}
}
