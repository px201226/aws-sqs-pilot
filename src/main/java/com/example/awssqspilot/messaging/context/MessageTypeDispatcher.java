package com.example.awssqspilot.messaging.context;


import com.example.awssqspilot.messaging.message.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.InvocationTargetException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageTypeDispatcher {

	private final MessageTypeMappingMethodProcessor processor;
	private final ApplicationContext applicationContext;
	private final ObjectMapper objectMapper;

	public Object doDispatch(MessageType messageType, Object payLoad) {

		final var advice = processor.getMessageTypeAdvice(messageType);

		if (advice == null) {
			log.info("not found MessageTypeMapping bean for EventType. eventType = {}", messageType);
			return null;
		}

		final var beanName = advice.getBeanName();
		final var method = advice.getMethod();
		final var targetBean = getTargetBean(beanName);

		if (targetBean == null) {
			return null;
		}

		ReflectionUtils.makeAccessible(method);

		try {
			final Class<?>[] parameterTypes = method.getParameterTypes();
			final Class<?> parameterType = parameterTypes[0];
			if (parameterType.isAssignableFrom(String.class)) {
				return method.invoke(targetBean, payLoad);
			}

			final Object o = objectMapper.readValue(payLoad.toString(), parameterType);
			return method.invoke(targetBean, o);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		return null;

	}

	private Object getTargetBean(String beanName) {
		return applicationContext.getBean(beanName);
	}
}
