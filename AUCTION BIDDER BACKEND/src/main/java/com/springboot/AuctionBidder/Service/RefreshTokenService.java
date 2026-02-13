package com.springboot.AuctionBidder.Service;

import java.util.Optional;

import com.springboot.AuctionBidder.Entity.RefreshToken;
import com.springboot.AuctionBidder.dto.AuthResponse;
import com.springboot.AuctionBidder.dto.RefreshTokenRequest;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String email);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);

    int deleteByUserId(Long userId);
}
