package com.springboot.AuctionBidder.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.AuctionBidder.Entity.User;

public interface UserRepository extends JpaRepository<User,Long>{
	boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);

}
