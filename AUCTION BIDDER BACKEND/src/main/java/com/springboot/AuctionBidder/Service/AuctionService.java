package com.springboot.AuctionBidder.Service;

import java.util.List;

import com.springboot.AuctionBidder.Entity.Auction;

public interface AuctionService {

    List<Auction> getLiveAuctions(String userEmail);

    Auction getAuctionById(Long id, String userEmail);

    Auction createAuction(Auction auction, String adminEmail);

    Auction updateAuction(Auction auction);
}
