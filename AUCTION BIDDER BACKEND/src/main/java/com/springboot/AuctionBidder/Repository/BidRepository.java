package com.springboot.AuctionBidder.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.AuctionBidder.Entity.Auction;
import com.springboot.AuctionBidder.Entity.Bid;
import com.springboot.AuctionBidder.Entity.User;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionAuctionIdOrderByAmountDesc(Long auctionId);

    Optional<Bid> findTopByAuction_AuctionIdOrderByAmountDesc(Long auctionId);

    List<Bid> findByAuction(Auction auction);

    Bid findTopByAuctionOrderByAmountDesc(Auction auction);

    List<Bid> findByBidder(User bidder);

    Bid findTopByAuctionAndBidderOrderByAmountDesc(Auction auction, User bidder);

}
