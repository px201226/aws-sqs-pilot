package com.example.awssqspilot.springboot.messaging.context;


import com.example.awssqspilot.domain.event.EventType;
import java.lang.reflect.InvocationTargetException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventTypeDispatcher {

	private final EventTypeMappingMethodProcessor processor;
	private final ApplicationContext applicationContext;

	public Object doDispatch(EventType eventType, Object payLoad) {

		final var eventTypeAdvice = processor.getEventTypeAdvice(eventType);

		final var beanName = eventTypeAdvice.getBeanName();
		final var method = eventTypeAdvice.getMethod();
		final var targetBean = getTargetBean(beanName);

		if (targetBean == null) {
			return null;
		}

		ReflectionUtils.makeAccessible(method);

		try {
			return method.invoke(payLoad);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;

	}

	private Object getTargetBean(String beanName) {
		return applicationContext.getBean(beanName);
	}
}
