package com.springboot.AuctionBidder.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.AuctionBidder.Entity.Auction;
import com.springboot.AuctionBidder.Service.AuctionService;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @GetMapping
    public List<Auction> getAllAuctions(Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        return auctionService.getLiveAuctions(email);
    }

    @GetMapping("/{id}")
    public Auction getAuction(@PathVariable Long id, Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        return auctionService.getAuctionById(id, email);
    }

    @PostMapping
    public ResponseEntity<Auction> createAuction(@RequestBody Auction auction, Principal principal) {
        Auction createdAuction = auctionService.createAuction(auction, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuction);
    }

}
