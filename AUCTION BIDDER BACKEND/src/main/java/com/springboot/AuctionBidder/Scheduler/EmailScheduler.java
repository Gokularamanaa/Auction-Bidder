package com.springboot.AuctionBidder.Scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.springboot.AuctionBidder.Service.EmailService;

@Component
public class EmailScheduler {

	@Autowired
	private EmailService emailService;
	
	@Scheduled(cron = "0 0 1 * * ?")
	public void sendScheduledEmail() {
		String[] recepients = {"gokularamanaa23@gmail.com"};
		
		emailService.sendMail(recepients ,  "Scheduled Email from Spring Boot",
				"Hello! This is a scheduled email sent automatically every 10 minutes.");
	}
}
