package com.springboot.AuctionBidder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.AuctionBidder.Entity.Auction;
import com.springboot.AuctionBidder.Entity.AuctionStatus;
import com.springboot.AuctionBidder.Entity.Role;
import com.springboot.AuctionBidder.Entity.User;
import com.springboot.AuctionBidder.Repository.AuctionRepository;
import com.springboot.AuctionBidder.Repository.UserRepository;
import com.springboot.AuctionBidder.Util.JwtUtil;
import com.springboot.AuctionBidder.dto.RegisterRequest;

@SpringBootTest
@AutoConfigureMockMvc
class BidIntegrationTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private AuctionRepository auctionRepository;

        @Autowired
        private com.springboot.AuctionBidder.Repository.BidRepository bidRepository;

        @Autowired
        private JwtUtil jwtUtil;

        @org.springframework.boot.test.mock.mockito.MockBean
        private org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

        private User testUser;
        private User adminUser;
        private Auction liveAuction;
        private String userToken;
        private String adminToken;

        @BeforeEach
        void setUp() {
                bidRepository.deleteAll();
                auctionRepository.deleteAll();
                userRepository.deleteAll();

                // Create test user
                testUser = new User();
                testUser.setName("Test User");
                testUser.setEmail("testuser@example.com");
                testUser.setPassword("password123");
                testUser.addRole(Role.ROLE_USER);
                testUser = userRepository.save(testUser);
                userToken = jwtUtil.generateAccessToken(testUser);

                // Create admin user
                adminUser = new User();
                adminUser.setName("Admin User");
                adminUser.setEmail("admin@example.com");
                adminUser.setPassword("admin123");
                adminUser.addRole(Role.ROLE_ADMIN);
                adminUser = userRepository.save(adminUser);
                adminToken = jwtUtil.generateAccessToken(adminUser);

                // Create live auction
                liveAuction = new Auction();
                liveAuction.setDescription("Test Auction");
                liveAuction.setStartingPrice(1000.0);
                liveAuction.setAuctionStartTime(LocalDateTime.now().minusHours(1));
                liveAuction.setAuctionEndTime(LocalDateTime.now().plusHours(2));
                liveAuction.setStatus(AuctionStatus.LIVE);
                liveAuction.setCreatedBy(adminUser);
                liveAuction = auctionRepository.save(liveAuction);
        }

        @Test
        void testPlaceBidSuccess() throws Exception {
                BigDecimal bidAmount = BigDecimal.valueOf(1100.0);

                mockMvc.perform(post("/bids/" + liveAuction.getAuctionId())
                                .param("amount", bidAmount.toString())
                                .header("Authorization", "Bearer " + userToken)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.amount").value(1100.0));
        }

        @Test
        void testPlaceBidWithoutToken() throws Exception {
                BigDecimal bidAmount = BigDecimal.valueOf(1100.0);

                mockMvc.perform(post("/bids/" + liveAuction.getAuctionId())
                                .param("amount", bidAmount.toString())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isForbidden());
        }

        @Test
        void testPlaceBidBelowMinimumIncrement() throws Exception {
                // First bid
                BigDecimal firstBid = BigDecimal.valueOf(1100.0);
                mockMvc.perform(post("/bids/" + liveAuction.getAuctionId())
                                .param("amount", firstBid.toString())
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isOk());

                // Create another user for second bid
                User user2 = new User();
                user2.setName("User 2");
                user2.setEmail("user2@example.com");
                user2.setPassword("password123");
                user2.addRole(Role.ROLE_USER);
                user2 = userRepository.save(user2);
                String user2Token = jwtUtil.generateAccessToken(user2);

                // Second bid below minimum increment
                BigDecimal secondBid = BigDecimal.valueOf(1150.0); // Only 50 increment, minimum is 100
                mockMvc.perform(post("/bids/" + liveAuction.getAuctionId())
                                .param("amount", secondBid.toString())
                                .header("Authorization", "Bearer " + user2Token))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testGetBidsForAuction() throws Exception {
                // Place a bid
                BigDecimal bidAmount = BigDecimal.valueOf(1100.0);
                mockMvc.perform(post("/bids/" + liveAuction.getAuctionId())
                                .param("amount", bidAmount.toString())
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isOk());

                // Retrieve bids
                mockMvc.perform(get("/bids/" + liveAuction.getAuctionId())
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        @Test
        void testPlaceBid_Sequential() throws Exception {
                // 1. User 1 places a bid -> version increments
                mockMvc.perform(post("/bids/" + liveAuction.getAuctionId())
                                .param("amount", "1100.0")
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isOk());

                // 2. User 2 places a higher bid
                mockMvc.perform(post("/bids/" + liveAuction.getAuctionId())
                                .param("amount", "1200.0")
                                .header("Authorization", "Bearer " + adminToken)) // Using admin as second user
                                .andExpect(status().isOk());

                // And verify getting the highest bid returns correct amount
                mockMvc.perform(get("/bids/" + liveAuction.getAuctionId() + "/highest")
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.amount").value(1200.0));
        }
}
