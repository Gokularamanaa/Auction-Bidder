package com.springboot.AuctionBidder.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.AuctionBidder.Entity.User;
import com.springboot.AuctionBidder.Repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

   
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
