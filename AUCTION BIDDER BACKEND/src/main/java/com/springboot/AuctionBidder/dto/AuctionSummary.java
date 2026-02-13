/*package com.springboot.AuctionBidder.dto;

import java.math.BigDecimal;

import com.springboot.AuctionBidder.Entity.Auction;
import com.springboot.AuctionBidder.Entity.Bid;

public class AuctionSummary {
    
    private Long auctionId;
    private String title;
    private String status;
    private String highestBidder;
    private BigDecimal highestBid;

    public static AuctionSummary from(Auction auction, Bid highestBid) {
        AuctionSummary dto = new AuctionSummary();
        dto.auctionId = auction.getId();
        dto.title = auction.getTitle();
        dto.status = auction.getStatus().name();
        dto.highestBid = highestBid.getAmount();
        dto.highestBidder = highestBid.getBidder().getEmail();
        return dto;
    }
}*/
