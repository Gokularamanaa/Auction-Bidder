package com.springboot.AuctionBidder.Service;

import java.util.Map;

public interface DashboardService {

	Map<String, Object> getUserDashboard(String email);
}
