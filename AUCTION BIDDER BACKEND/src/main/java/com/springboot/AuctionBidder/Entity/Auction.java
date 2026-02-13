package com.springboot.AuctionBidder.Entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "auction")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Auction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long auctionId;

	@jakarta.persistence.Version
	@Column(name = "version")
	private Long version;

	public Long getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(Long auctionId) {
		this.auctionId = auctionId;
	}

	@Column(name = "description")
	private String description;

	@Column(name = "starting_price")
	private Double startingPrice;

	@Column(name = "current_high_bid")
	private java.math.BigDecimal currentHighBid;

	public void setCurrentHighBid(java.math.BigDecimal currentHighBid) {
		this.currentHighBid = currentHighBid;
	}

	public java.math.BigDecimal getCurrentHighBid() {
		return currentHighBid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getStartingPrice() {
		return startingPrice;
	}

	public void setStartingPrice(Double startingPrice) {
		this.startingPrice = startingPrice;
	}

	public AuctionStatus getStatus() {
		return status;
	}

	public void setStatus(AuctionStatus status) {
		this.status = status;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getAuctionStartTime() {
		return auctionStartTime;
	}

	public void setAuctionStartTime(LocalDateTime auctionStartTime) {
		this.auctionStartTime = auctionStartTime;
	}

	public LocalDateTime getAuctionEndTime() {
		return auctionEndTime;
	}

	public void setAuctionEndTime(LocalDateTime auctionEndTime) {
		this.auctionEndTime = auctionEndTime;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private AuctionStatus status;

	@com.fasterxml.jackson.annotation.JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User createdBy;

	@JsonProperty("startTime")
	private LocalDateTime auctionStartTime;

	@JsonProperty("endTime")
	private LocalDateTime auctionEndTime;

	@Column(name = "title")
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@jakarta.persistence.Transient
	private java.math.BigDecimal userBid;

	public java.math.BigDecimal getUserBid() {
		return userBid;
	}

	public void setUserBid(java.math.BigDecimal userBid) {
		this.userBid = userBid;
	}

}
