package com.springboot.AuctionBidder.dto;

public class AuthResponse {

    private String accessToken;
    private java.util.Set<String> roles;
    private String message;



    private String refreshToken;

    public AuthResponse(String accessToken, String refreshToken, java.util.Set<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.roles = roles;
    }

    // Legacy constructor compatibility if needed (or just deprecate it)
    public AuthResponse(String accessToken, java.util.Set<String> roles) {
        this.accessToken = accessToken;
        this.roles = roles;
    }

    public AuthResponse(String message) {
        this.message = message;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public java.util.Set<String> getRoles() {
        return roles;
    }

    public void setRoles(java.util.Set<String> roles) {
        this.roles = roles;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
