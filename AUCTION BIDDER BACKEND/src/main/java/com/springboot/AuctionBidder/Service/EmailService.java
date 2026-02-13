package com.springboot.AuctionBidder.Service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

	public void sendMail(String[] recepients, String subject, String header);

	public void sendWinnerNotification(String to, String auctionTitle, Double amount);
}
