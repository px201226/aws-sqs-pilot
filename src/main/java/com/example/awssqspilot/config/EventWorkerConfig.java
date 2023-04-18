package com.example.awssqspilot.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@RequiredArgsConstructor
public class EventWorkerConfig {

	private final TransactionTemplate transactionTemplate;
	private static final int THREAD_WAIT_TIME = 5000; // mill second
	private static int poolSize = 10;

	@Bean(name = "eventWorkerPool")
	public ThreadPoolTaskExecutor eventWorkerPool() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setMaxPoolSize(poolSize);
		executor.setQueueCapacity(0);
//		executor.setTaskDecorator(new TransactionTaskDecorator(transactionTemplate));
		executor.setRejectedExecutionHandler(new BlockingTaskSubmissionPolicy(THREAD_WAIT_TIME));
		return executor;
	}


	private record TransactionTaskDecorator(TransactionTemplate transactionTemplate) implements TaskDecorator {

		@Override public Runnable decorate(final Runnable runnable) {
			return () -> {
				transactionTemplate.executeWithoutResult(
						status -> {
							try {
								runnable.run();
							} catch (Exception e) {
								System.out.println("dddddd");
								e.printStackTrace();
								throw e;/**/
							}
						}
				);
			};
		}
	}

	private record BlockingTaskSubmissionPolicy(long timeout) implements RejectedExecutionHandler {

		/**
		 * @param timeout MILLISECONDS
		 */
		private BlockingTaskSubmissionPolicy {
		}

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			try {
				BlockingQueue queue = executor.getQueue();
				//As we are expecting the value of thread pool queue is 0
				if (!queue.offer(r, this.timeout, TimeUnit.MILLISECONDS)) {
					throw new RejectedExecutionException("The Thread Pool is full");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}


}