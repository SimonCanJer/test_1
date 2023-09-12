package com.intuit.test.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.ReactiveManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication()
@ComponentScans({@ComponentScan("${config.data.source}"),@ComponentScan("${config.data.dao}"),@ComponentScan("${config.rest.endpoint}"),@ComponentScan("${config.security.package}"),@ComponentScan("com.intuit.test.spring.file_source.scheduler"),@ComponentScan("com.intuit.test.spring.cache.grid")})
public class TestApplication {

	public static void main(String[] args) {

		SpringApplication.run(TestApplication.class, args);

	}

}
