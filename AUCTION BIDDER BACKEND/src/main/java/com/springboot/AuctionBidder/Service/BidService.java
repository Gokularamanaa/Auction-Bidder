package com.springboot.AuctionBidder.Service;

import java.math.BigDecimal;
import java.util.List;

import com.springboot.AuctionBidder.Entity.Bid;

public interface BidService {
	Bid placeBid(Long auctionId, BigDecimal amount, String userEmail);
    List<Bid> getBidsForAuction(Long auctionId);
    Bid getHighestBid(Long auctionId);
    
}
