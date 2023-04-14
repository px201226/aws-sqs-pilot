package com.example.awssqspilot.springboot.listener;

import com.example.awssqspilot.util.AcknowledgmentUtils;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class SqsMessageHandler {

	private final AsyncTaskExecutor eventWorkerPool;

	public <T, R> void handle(
			final T t,
			final MessageHeaders messageHeaders,
			final Acknowledgment acknowledgment,
			final Callable<R> callable,
			final MessageHandlerCallback<T, R> callback
	) {
		eventWorkerPool.submit(() -> {

			if (callback != null) {
				callback.onStart(t, messageHeaders);
			}

			final R result;
			try {
				result = callable.call();
				AcknowledgmentUtils.ack(acknowledgment);
			} catch (Exception e) {

				if (callback != null) {
					callback.onError(t, messageHeaders, e);
				}

				throw new RuntimeException(e);
			}

			if (callback != null) {
				callback.onComplete(t, messageHeaders, result);
			}

		});

	}


}
