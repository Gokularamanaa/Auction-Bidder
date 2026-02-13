package com.springboot.AuctionBidder.Config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.springboot.AuctionBidder.Entity.Auction;
import com.springboot.AuctionBidder.Repository.AuctionRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AuctionRepository auctionRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Auction> auctions = auctionRepository.findAll();
        boolean changed = false;

        for (Auction auction : auctions) {
            if (auction.getTitle() == null || auction.getTitle().trim().isEmpty()) {
                // Generate a title based on description or ID
                String newTitle = "Auction Item #" + auction.getAuctionId();
                if (auction.getDescription() != null && !auction.getDescription().isEmpty()) {
                    // Use start of description if available, max 30 chars
                    newTitle = auction.getDescription().substring(0, Math.min(auction.getDescription().length(), 30));
                    if (auction.getDescription().length() > 30) {
                        newTitle += "...";
                    }
                }
                auction.setTitle(newTitle);
                changed = true;
                System.out
                        .println("Migrated Auction ID " + auction.getAuctionId() + ": Set title to '" + newTitle + "'");
            }
        }

        if (changed) {
            auctionRepository.saveAll(auctions);
            System.out.println("Data Migration Completed: Updated missing titles.");
        }
    }
}
