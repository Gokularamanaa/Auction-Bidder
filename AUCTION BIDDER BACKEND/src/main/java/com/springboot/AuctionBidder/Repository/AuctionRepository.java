package com.springboot.AuctionBidder.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.AuctionBidder.Entity.Auction;
import com.springboot.AuctionBidder.Entity.AuctionStatus;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
	List<Auction> findByStatus(AuctionStatus status);

	List<Auction> findByAuctionEndTimeBeforeAndStatus(LocalDateTime time, AuctionStatus status);

	List<Auction> findByAuctionStartTimeBeforeAndStatus(LocalDateTime time, AuctionStatus status);

	List<Auction> findByStatusIn(List<AuctionStatus> statuses);
}
