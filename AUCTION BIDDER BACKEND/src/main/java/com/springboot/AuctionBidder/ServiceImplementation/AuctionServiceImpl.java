package com.springboot.AuctionBidder.ServiceImplementation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.AuctionBidder.Entity.Auction;
import com.springboot.AuctionBidder.Entity.AuctionStatus;
import com.springboot.AuctionBidder.Entity.User;
import com.springboot.AuctionBidder.Repository.AuctionRepository;
import com.springboot.AuctionBidder.Repository.UserRepository;
import com.springboot.AuctionBidder.Service.AuctionService;

@Service
public class AuctionServiceImpl implements AuctionService {

	@Autowired
	private AuctionRepository auctionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private com.springboot.AuctionBidder.Repository.BidRepository bidRepository;

	@Override
	public List<Auction> getLiveAuctions(String userEmail) {
		List<Auction> auctions = auctionRepository.findByStatusIn(List.of(AuctionStatus.LIVE, AuctionStatus.UPCOMING));

		if (userEmail != null) {
			User user = userRepository.findByEmail(userEmail).orElse(null);
			if (user != null) {
				for (Auction auction : auctions) {
					com.springboot.AuctionBidder.Entity.Bid highestBid = bidRepository
							.findTopByAuctionAndBidderOrderByAmountDesc(auction, user);
					if (highestBid != null) {
						auction.setUserBid(highestBid.getAmount());
					}
				}
			}
		}
		return auctions;
	}

	@Override
	public Auction getAuctionById(Long id, String userEmail) {
		Auction auction = auctionRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Auction not found"));

		if (userEmail != null) {
			User user = userRepository.findByEmail(userEmail).orElse(null);
			if (user != null) {
				com.springboot.AuctionBidder.Entity.Bid highestBid = bidRepository
						.findTopByAuctionAndBidderOrderByAmountDesc(auction, user);
				if (highestBid != null) {
					auction.setUserBid(highestBid.getAmount());
				}
			}
		}
		return auction;
	}

	@Override
	@Transactional
	public Auction createAuction(Auction auction, String adminEmail) {
		User admin = userRepository.findByEmail(adminEmail)
				.orElseThrow(() -> new RuntimeException("Admin user not found"));

		auction.setCreatedBy(admin);

		if (auction.getAuctionStartTime() == null) {
			auction.setAuctionStartTime(LocalDateTime.now());
		}

		if (auction.getAuctionEndTime() == null) {
			auction.setAuctionEndTime(auction.getAuctionStartTime().plusHours(24));
		}

		if (auction.getAuctionStartTime().isBefore(LocalDateTime.now())) {
			auction.setStatus(AuctionStatus.LIVE);
		} else {
			auction.setStatus(AuctionStatus.UPCOMING);
		}

		return auctionRepository.save(auction);
	}

	@Override
	public Auction updateAuction(Auction auction) {
		return auctionRepository.save(auction);
	}

	@org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
	@Transactional
	public void updateAuctionStatuses() {
		LocalDateTime now = LocalDateTime.now();

		// Start UPCOMING auctions
		List<Auction> toStart = auctionRepository.findByAuctionStartTimeBeforeAndStatus(now, AuctionStatus.UPCOMING);
		for (Auction a : toStart) {
			a.setStatus(AuctionStatus.LIVE);
			System.out.println("Auto-Started Auction ID: " + a.getAuctionId());
		}
		if (!toStart.isEmpty()) {
			auctionRepository.saveAll(toStart);
		}

		// End LIVE auctions
		List<Auction> toEnd = auctionRepository.findByAuctionEndTimeBeforeAndStatus(now, AuctionStatus.LIVE);
		for (Auction a : toEnd) {
			a.setStatus(AuctionStatus.ENDED);
			System.out.println("Auto-Ended Auction ID: " + a.getAuctionId());
		}
		if (!toEnd.isEmpty()) {
			auctionRepository.saveAll(toEnd);
		}
	}
}
