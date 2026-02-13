package com.springboot.AuctionBidder.Controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.AuctionBidder.Entity.Bid;
import com.springboot.AuctionBidder.Service.BidService;

@RestController
@RequestMapping("/bids")
public class BidController {

	@Autowired
    private BidService bidService;

    @PostMapping("/{auctionId}")
    public ResponseEntity<Bid> placeBid(@PathVariable Long auctionId,@RequestParam BigDecimal amount,Principal principal) {

        Bid bid = bidService.placeBid(auctionId, amount, principal.getName());
        return ResponseEntity.ok(bid);
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<List<Bid>> getBids(@PathVariable Long auctionId) {
        return ResponseEntity.ok(bidService.getBidsForAuction(auctionId));
    }

    @GetMapping("/{auctionId}/highest")
    public ResponseEntity<Bid> getHighestBid(@PathVariable Long auctionId) {
        return ResponseEntity.ok(bidService.getHighestBid(auctionId));
    }
}
