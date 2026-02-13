package com.springboot.AuctionBidder.Scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.springboot.AuctionBidder.Entity.Auction;
import com.springboot.AuctionBidder.Entity.AuctionStatus;
import com.springboot.AuctionBidder.Repository.AuctionRepository;

@Component
@EnableScheduling
public class AuctionScheduler {

    @Autowired
    private AuctionRepository auctionRepo;

    @Autowired
    private com.springboot.AuctionBidder.Service.EmailService emailService;

    @Autowired
    private com.springboot.AuctionBidder.Service.BidService bidService;

    @Scheduled(fixedRate = 60000)
    public void closeExpiredAuctions() {

        List<Auction> expired = auctionRepo.findByAuctionEndTimeBeforeAndStatus(
                LocalDateTime.now(), AuctionStatus.LIVE);

        for (Auction auction : expired) {
            auction.setStatus(AuctionStatus.ENDED);
            auctionRepo.save(auction);

            // Notify Winner
            try {
                com.springboot.AuctionBidder.Entity.Bid highestBid = bidService.getHighestBid(auction.getAuctionId());
                if (highestBid != null && highestBid.getBidder() != null) {
                    String winnerEmail = highestBid.getBidder().getEmail();
                    String title = auction.getTitle() != null ? auction.getTitle()
                            : "Auction #" + auction.getAuctionId();
                    Double amount = highestBid.getAmount().doubleValue();

                    emailService.sendWinnerNotification(winnerEmail, title, amount);
                    System.out.println(
                            "Auto-closed auction " + auction.getAuctionId() + " and emailed winner " + winnerEmail);
                }
            } catch (Exception e) {
                System.err.println("Failed to send auto-close email for auction " + auction.getAuctionId() + ": "
                        + e.getMessage());
            }
        }
    }
}
