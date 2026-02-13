package com.springboot.AuctionBidder.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.AuctionBidder.Entity.User;
import com.springboot.AuctionBidder.Service.AuthService;
import com.springboot.AuctionBidder.Repository.UserRepository;
import com.springboot.AuctionBidder.Util.JwtUtil;
import com.springboot.AuctionBidder.dto.AuthRequestdto;
import com.springboot.AuctionBidder.dto.AuthResponse;
import com.springboot.AuctionBidder.dto.RegisterRequest;
import com.springboot.AuctionBidder.Entity.Role;
import java.util.stream.Collectors;
import java.util.Set;

import com.springboot.AuctionBidder.Entity.RefreshToken;
import com.springboot.AuctionBidder.Service.RefreshTokenService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequestdto request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getEmail());

            Set<String> roles = user.getRoles().stream()
                    .map(Role::name)
                    .collect(Collectors.toSet());

            return ResponseEntity.ok(new AuthResponse(token, refreshToken.getToken(), roles));

        } catch (AuthenticationException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid email or password"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody com.springboot.AuctionBidder.dto.RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtil.generateAccessToken(user);
                    return ResponseEntity.ok(new AuthResponse(token, requestRefreshToken,
                            user.getRoles().stream().map(Role::name).collect(Collectors.toSet())));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    // ================= REGISTER =================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {
            User user = authService.register(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new AuthResponse("User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }
}
