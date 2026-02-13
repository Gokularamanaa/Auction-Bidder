package com.springboot.AuctionBidder.ServiceImplementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.springboot.AuctionBidder.Service.EmailService;

@Service
public class EmailServiceImplementation implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendMail(String[] recepients, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com");
        message.setTo(recepients);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);
        System.out.println("Mail Sent successfully...");

    }

    @Override
    public void sendWinnerNotification(String to, String auctionTitle, Double amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com");
        message.setTo(to);
        message.setSubject("Congratulations! You won the auction: " + auctionTitle);
        message.setText("Dear Winner,\n\n" +
                "Congratulations! You have won the auction for '" + auctionTitle + "'.\n" +
                "Your winning bid was $" + amount + ".\n\n" +
                "PAYMENT INSTRUCTIONS:\n" +
                "Please proceed to your dashboard to complete the payment via Credit Card or UPI.\n" +
                "Payment Link: http://localhost:5173/dashboard\n\n" +
                "Best regards,\nAuctionBidder Team");

        try {
            mailSender.send(message);
            System.out.println("Winner notification sent to " + to);
        } catch (Exception e) {
            System.err.println("Failed to send winner email: " + e.getMessage());
        }
    }
}
