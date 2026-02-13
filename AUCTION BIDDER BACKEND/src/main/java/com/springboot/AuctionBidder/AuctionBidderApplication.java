package com.springboot.AuctionBidder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.springboot.AuctionBidder")
@EnableScheduling
public class AuctionBidderApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionBidderApplication.class, args);
	}

}
