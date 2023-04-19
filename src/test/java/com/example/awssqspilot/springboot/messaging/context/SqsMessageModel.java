package com.example.awssqspilot.springboot.messaging.context;


import com.example.awssqspilot.messaging.concrete.sqs.SqsMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqsMessageModel implements SqsMessage {

	private String payload;
	private String eventId;

	@Override public String getPayload() {
		return payload;
	}

	@Override public String getEventId() {
		return eventId;
	}


}
