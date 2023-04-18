package com.example.awssqspilot.messaging.concrete.sqs;

public interface SqsMessage {

	String getPayload();

	String getEventId();


}
