package com.sqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.sqs")
@SpringBootApplication(exclude = {ContextStackAutoConfiguration.class})
public class ServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicesApplication.class, args);
	}

}
