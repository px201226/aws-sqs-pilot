package com.example.awssqspilot.domain.dto;

import com.example.awssqspilot.domain.event.EventSource;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

public class DomainEventModel {


	@Data
	@AllArgsConstructor
	public static class RegisteredBizSlipTrade implements EventSource {

		private Long bizGroupNo;
		private String bizCd;
		private LocalDateTime regDt;
		private String eventId;
		private String messageGroupId;
		private String deduplicationId;

		@Override public Long getBizGroupNo() {
			return bizGroupNo;
		}

		@Override public String getBizCd() {
			return bizCd;
		}

		@Override public String getEventId() {
			return eventId;
		}

		@Override public String getEventGroupId() {
			return messageGroupId;
		}
	}
}
