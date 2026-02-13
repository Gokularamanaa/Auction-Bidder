package com.springboot.AuctionBidder.ServiceImplementation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.AuctionBidder.Entity.Auction;
import com.springboot.AuctionBidder.Entity.AuctionStatus;
import com.springboot.AuctionBidder.Entity.Bid;
import com.springboot.AuctionBidder.Entity.User;
import com.springboot.AuctionBidder.Repository.AuctionRepository;
import com.springboot.AuctionBidder.Repository.BidRepository;
import com.springboot.AuctionBidder.Repository.UserRepository;
import com.springboot.AuctionBidder.Service.BidService;

@Service
public class BidServiceImpl implements BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public Bid placeBid(Long auctionId, BigDecimal amount, String userEmail) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        if (auction.getStatus() != AuctionStatus.LIVE) {
            throw new RuntimeException("Auction is not LIVE");
        }

        if (auction.getAuctionEndTime().isBefore(LocalDateTime.now())) {
            auction.setStatus(AuctionStatus.ENDED);
            auctionRepository.save(auction);
            throw new RuntimeException("Auction has ended");
        }

        User bidder = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate bid amount against current highest bid on the Auction entity
        BigDecimal currentHighest = auction.getCurrentHighBid();

        // If no bids yet, check against starting price
        if (currentHighest == null) {
            currentHighest = BigDecimal.valueOf(auction.getStartingPrice());
            if (amount.compareTo(currentHighest) < 0) {
                throw new com.springboot.AuctionBidder.Exception.InvalidBidException(
                        "Bid must be at least starting price: " + auction.getStartingPrice());
            }
        } else {
            // Must be current highest + 100
            if (amount.compareTo(currentHighest.add(BigDecimal.valueOf(100))) < 0) {
                throw new com.springboot.AuctionBidder.Exception.InvalidBidException(
                        "Bid must be at least " + currentHighest.add(BigDecimal.valueOf(100)));
            }
        }

        // Update Auction to trigger Optimistic Locking (@Version check)
        auction.setCurrentHighBid(amount);
        auctionRepository.save(auction);

        // Save Bid
        Bid newBid = new Bid();
        newBid.setAuction(auction);
        newBid.setBidder(bidder);
        newBid.setAmount(amount);
        newBid.setBidTime(LocalDateTime.now());

        Bid savedBid = bidRepository.save(newBid);

        // Broadcast updated highest bid (or the full bid object)
        messagingTemplate.convertAndSend("/topic/auction/" + auctionId, savedBid);

        return savedBid;
    }

    @Override
    public List<Bid> getBidsForAuction(Long auctionId) {
        return bidRepository.findByAuctionAuctionIdOrderByAmountDesc(auctionId);
    }

    @Override
    public Bid getHighestBid(Long auctionId) {
        return bidRepository.findTopByAuction_AuctionIdOrderByAmountDesc(auctionId).orElse(null);
    }
}
