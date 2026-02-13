package com.springboot.AuctionBidder.ServiceImplementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.AuctionBidder.Entity.Auction;
import com.springboot.AuctionBidder.Entity.AuctionStatus;
import com.springboot.AuctionBidder.Entity.Bid;
import com.springboot.AuctionBidder.Entity.User;
import com.springboot.AuctionBidder.Repository.AuctionRepository;
import com.springboot.AuctionBidder.Repository.BidRepository;
import com.springboot.AuctionBidder.Repository.UserRepository;
import com.springboot.AuctionBidder.Service.DashboardService;

@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Override
    public Map<String, Object> getUserDashboard(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get all bids made by this user
        List<Bid> myBids = bidRepository.findByBidder(user);
        System.out.println("Dashboard Debug: Found " + myBids.size() + " bids for user " + userEmail);
        myBids.forEach(b -> System.out.println("Bid ID: " + b.getId() + ", Amount: " + b.getAmount() + ", Auction ID: "
                + (b.getAuction() != null ? b.getAuction().getAuctionId() : "null")));

        // Get all auctions where this user has placed a bid
        // Get unique auction IDs from user's bids
        java.util.Set<Long> auctionIds = myBids.stream()
                .map(bid -> bid.getAuction().getAuctionId())
                .collect(Collectors.toSet());

        // Fetch distinct auctions from repository
        List<Auction> auctionsWithBids = auctionRepository.findAllById(auctionIds);

        List<Auction> won = new ArrayList<>();
        List<Auction> leading = new ArrayList<>();
        List<Auction> trailing = new ArrayList<>();

        // Process each auction
        for (Auction auction : auctionsWithBids) {
            // Set user's bid for this auction
            java.math.BigDecimal userMaxBid = myBids.stream()
                    .filter(b -> b.getAuction().getAuctionId().equals(auction.getAuctionId()))
                    .map(Bid::getAmount)
                    .max(java.math.BigDecimal::compareTo)
                    .orElse(null);

            if (userMaxBid != null) {
                auction.setUserBid(userMaxBid);
            }

            // Get the highest bid for this auction
            Bid highestBid = bidRepository.findTopByAuction_AuctionIdOrderByAmountDesc(auction.getAuctionId())
                    .orElse(null);

            if (highestBid == null)
                continue;

            boolean isHighestBidder = highestBid.getBidder().getId().equals(user.getId());

            if (auction.getStatus() == AuctionStatus.ENDED) {
                // Won auctions: user has highest bid and auction is ended
                if (isHighestBidder) {
                    won.add(auction);
                }
            } else if (auction.getStatus() == AuctionStatus.LIVE) {
                // Live auctions: separate leading and trailing
                if (isHighestBidder) {
                    leading.add(auction);
                } else {
                    trailing.add(auction);
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("won", won);
        response.put("leading", leading);
        response.put("trailing", trailing);

        return response;
    }
}
