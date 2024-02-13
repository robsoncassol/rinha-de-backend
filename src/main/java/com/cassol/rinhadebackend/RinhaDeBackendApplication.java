package com.cassol.rinhadebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class RinhaDeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RinhaDeBackendApplication.class, args);
	}

}
