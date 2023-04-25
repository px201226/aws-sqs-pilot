package com.example.awssqspilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		scanBasePackages = {
				"com.example.awssqspilot",
				"com.marketboro2.advancesqs"
		}
)
public class AwsSqsPilotApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsSqsPilotApplication.class, args);
	}

}
