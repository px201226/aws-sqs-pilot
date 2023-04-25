package com.marketboro2.advancesqs.messaging.concrete.sqs;

public interface SqsMessage {

	String getPayload();

	String getEventId();


}
