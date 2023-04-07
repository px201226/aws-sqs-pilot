package com.example.awssqspilot.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.context.SpringBootTest;

@Nested
@SpringBootTest
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NestedSpringTest {


}
