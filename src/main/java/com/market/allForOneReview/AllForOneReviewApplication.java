package com.market.allForOneReview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AllForOneReviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(AllForOneReviewApplication.class, args);
	}

}
