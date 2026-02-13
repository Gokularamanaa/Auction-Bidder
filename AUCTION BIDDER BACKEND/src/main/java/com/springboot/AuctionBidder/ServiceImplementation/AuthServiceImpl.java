package com.springboot.AuctionBidder.ServiceImplementation;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.AuctionBidder.Entity.Role;
import com.springboot.AuctionBidder.Entity.User;
import com.springboot.AuctionBidder.Repository.UserRepository;
import com.springboot.AuctionBidder.Service.AuthService;
import com.springboot.AuctionBidder.dto.RegisterRequest;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder encoder;

    public User register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            roles.add(Role.ROLE_ADMIN);
        } else {
            roles.add(Role.ROLE_USER);
        }
        user.setRoles(roles);

        return userRepo.save(user);
    }
}
