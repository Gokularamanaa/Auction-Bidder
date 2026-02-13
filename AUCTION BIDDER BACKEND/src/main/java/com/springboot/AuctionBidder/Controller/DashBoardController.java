package com.springboot.AuctionBidder.Controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.AuctionBidder.Service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashBoardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public Map<String, Object> getDashboard(Principal principal) {
        return dashboardService.getUserDashboard(principal.getName());
    }
}
