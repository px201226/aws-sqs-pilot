package com.example.awssqspilot.messaging.annotation;

import com.example.awssqspilot.domain.event.EventType;
import com.example.awssqspilot.messaging.message.MessageType;
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
public @interface MessageTypeMapping {


	String messageType();
}
