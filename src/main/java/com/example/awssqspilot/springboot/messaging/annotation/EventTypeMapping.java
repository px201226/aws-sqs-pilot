package com.example.awssqspilot.springboot.messaging.annotation;

import com.example.awssqspilot.domain.event.EventType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface EventTypeMapping {

	EventType eventType();
}
