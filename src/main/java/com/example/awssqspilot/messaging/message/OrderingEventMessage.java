package com.example.awssqspilot.messaging.message;

public interface OrderingEventMessage {

	String getEventId();
	String getMessageGroupId();

	String getDeduplicationId();

}
