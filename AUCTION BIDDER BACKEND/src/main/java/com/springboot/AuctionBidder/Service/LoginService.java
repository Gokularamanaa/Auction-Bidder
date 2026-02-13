package com.springboot.AuctionBidder.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.springboot.AuctionBidder.Entity.User;
import com.springboot.AuctionBidder.Repository.UserRepository;
import com.springboot.AuctionBidder.Util.JwtUtil;

@Service
public class LoginService {

    @Autowired 
    AuthenticationManager authManager;
    @Autowired 
    JwtUtil jwtUtil;
    @Autowired 
    UserRepository userRepo;

    public String login(String email, String password) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = userRepo.findByEmail(email).orElseThrow();
        return jwtUtil.generateAccessToken(user);
    }
}
