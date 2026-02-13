package com.springboot.AuctionBidder.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.AuctionBidder.Entity.Auction;
import com.springboot.AuctionBidder.Entity.AuctionStatus;
import com.springboot.AuctionBidder.Service.AuctionService;

@RestController
@RequestMapping("/admin/auctions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuctionController {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private com.springboot.AuctionBidder.Service.EmailService emailService;

    @Autowired
    private com.springboot.AuctionBidder.Service.BidService bidService;

    /**
     * Create a new auction (admin only)
     * POST /admin/auctions
     */
    @PostMapping
    public ResponseEntity<Auction> createAuction(
            @RequestBody Auction auction,
            Principal principal) {

        Auction createdAuction = auctionService.createAuction(auction, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuction);
    }

    /**
     * Start an auction (transition from UPCOMING to LIVE)
     * PUT /admin/auctions/{auctionId}/start
     */
    @PutMapping("/{auctionId}/start")
    public ResponseEntity<Auction> startAuction(@PathVariable Long auctionId) {

        Auction auction = auctionService.getAuctionById(auctionId, null);

        if (auction.getStatus() != AuctionStatus.UPCOMING) {
            return ResponseEntity.badRequest().build();
        }

        auction.setStatus(AuctionStatus.LIVE);
        Auction updated = auctionService.updateAuction(auction);

        return ResponseEntity.ok(updated);
    }

    /**
     * End an auction manually
     * PUT /admin/auctions/{auctionId}/end
     */
    @PutMapping("/{auctionId}/end")
    public ResponseEntity<Auction> endAuction(@PathVariable Long auctionId) {

        Auction auction = auctionService.getAuctionById(auctionId, null);

        if (auction.getStatus() == AuctionStatus.ENDED) {
            return ResponseEntity.badRequest().build();
        }

        auction.setStatus(AuctionStatus.ENDED);
        Auction updated = auctionService.updateAuction(auction);

        // Find winner and send email
        try {
            com.springboot.AuctionBidder.Entity.Bid highestBid = bidService.getHighestBid(auctionId);
            if (highestBid != null && highestBid.getBidder() != null) {
                String winnerEmail = highestBid.getBidder().getEmail();
                String title = auction.getTitle() != null ? auction.getTitle() : "Auction #" + auction.getAuctionId();
                Double amount = highestBid.getAmount().doubleValue();

                emailService.sendWinnerNotification(winnerEmail, title, amount);
            }
        } catch (Exception e) {
            System.err.println("Error processing winner email: " + e.getMessage());
        }

        return ResponseEntity.ok(updated);
    }
}
