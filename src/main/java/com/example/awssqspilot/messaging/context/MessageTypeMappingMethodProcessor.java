package com.example.awssqspilot.messaging.context;


import com.example.awssqspilot.messaging.annotation.MessageTypeMapping;
import com.example.awssqspilot.messaging.message.MessageType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageTypeMappingMethodProcessor {


	private final ConfigurableListableBeanFactory beanFactory;
	private final Map<MessageType, MessageTypeAdvice> messageTypeAdviceMap;


	public MessageTypeAdvice getMessageTypeAdvice(com.example.awssqspilot.messaging.message.MessageType messageType) {
		return messageTypeAdviceMap.get(messageType);
	}

	@PostConstruct
	private void afterSingletonsInstantiated() {
		ConfigurableListableBeanFactory beanFactory = this.beanFactory;
		Assert.state(this.beanFactory != null, "No ConfigurableListableBeanFactory set");
		String[] beanNames = beanFactory.getBeanNamesForType(Object.class);
		for (String beanName : beanNames) {
			if (!ScopedProxyUtils.isScopedTarget(beanName)) {
				Class<?> type = AutoProxyUtils.determineTargetClass(beanFactory, beanName);
				processBean(beanName, type);
			}
		}
	}

	private void processBean(final String beanName, final Class<?> targetType) {
		if (AnnotationUtils.isCandidateClass(targetType, EventListener.class) &&
				!isSpringContainerClass(targetType)) {

			Map<Method, MessageTypeMapping> annotatedMethods = null;

			final var methodToAnnotation = MethodIntrospector.selectMethods(targetType,
					(MetadataLookup<MessageTypeMapping>) method ->
							AnnotatedElementUtils.findMergedAnnotation(method, MessageTypeMapping.class));

			for (final Entry<Method, MessageTypeMapping> entry : methodToAnnotation.entrySet()) {
				final var annotation = entry.getValue();
				final var method = entry.getKey();

				if (messageTypeAdviceMap.containsKey(annotation.messageType())) {
					log.info("중복 정의 된 메시지 타입 : {}", annotation.messageType());
					throw new RuntimeException("MessageTypeMapping이 중복으로 정의되어 있음, " + annotation.messageType());
				}

				messageTypeAdviceMap.put(new MessageType(annotation.messageType()), new MessageTypeAdvice(beanName, method));

			}


		}
	}

	private static boolean isSpringContainerClass(Class<?> clazz) {
		return (clazz.getName().startsWith("org.springframework.") &&
				!AnnotatedElementUtils.isAnnotated(ClassUtils.getUserClass(clazz), Component.class));
	}

	@Getter
	@AllArgsConstructor
	protected static class MessageTypeAdvice {

		private String beanName;
		private Method method;

	}

}
