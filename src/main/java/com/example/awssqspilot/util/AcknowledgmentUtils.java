package com.example.awssqspilot.util;

import java.util.concurrent.ExecutionException;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;

public class AcknowledgmentUtils {

	/**
	 * ack 응답을 보내고 CheckedException을 UnCheckedException으로 변환한다.
	 * @param acknowledgment
	 */
	public static void ack(Acknowledgment acknowledgment){
		try {
			acknowledgment.acknowledge().get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
