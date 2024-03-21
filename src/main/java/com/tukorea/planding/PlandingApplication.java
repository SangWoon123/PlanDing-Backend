package com.tukorea.planding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PlandingApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlandingApplication.class, args);
	}

}
