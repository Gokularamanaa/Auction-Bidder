package com.springboot.AuctionBidder.Util;

// Standard Java and Spring imports
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.springboot.AuctionBidder.Entity.Role;
import com.springboot.AuctionBidder.Entity.User;

// JJWT (Java JWT Library) imports for token creation and parsing
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * @Component: Tells Spring to manage this class as a "Bean" (a managed object).
 * This allows you to @Autowired this utility into your Controllers or Filters.
 */
@Component
public class JwtUtil {

    /**
     * @Value: Pulls values from your 'application.properties' or 'application.yml' file.
     * jwt.secret: A long, random string used to sign the token.
     * jwt.access-token-expiration: Time in milliseconds until the token becomes invalid.
     */
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiry;

    // The 'Key' object is a cryptographic representation of your 'secret' string.
    private Key key;

    /**
     * @PostConstruct: This method runs automatically AFTER Spring injects the @Value fields.
     * It converts the raw secret string into a secure HMAC cryptographic key.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * FUNCTION: generateAccessToken
     * Purpose: Creates a new JWT string for a logged-in user.
     */
    public String generateAccessToken(User user) {
        // Map<String, Object> claims: Extra data (Payload) we want to store inside the token.
        Map<String, Object> claims = new HashMap<>();
        
        // We extract the user's roles and put them into the "roles" claim so the UI/Backend knows their permissions.
        claims.put("roles", user.getRoles()
                .stream()
                .map(Role::name)
                .collect(Collectors.toList()));

        /**
         * Jwts.builder(): Starts the process of building the token string.
         * .setClaims: Sets the custom data (roles).
         * .setSubject: Sets the primary identity (usually email or username).
         * .setIssuedAt: Records when the token was created.
         * .setExpiration: Sets the "Death Date" of the token.
         * .signWith: Signs the token using the Secret Key and HS256 algorithm to prevent tampering.
         * .compact(): Finalizes everything into a single encrypted string.
         */
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * FUNCTION: isTokenValid
     * Purpose: Checks if the token belongs to the user and hasn't expired.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        // Check 1: Does the email in the token match the user in the database?
        // Check 2: Is the current time before the expiration time?
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * FUNCTION: isTokenExpired
     * Purpose: Compares the token's expiration date with the current system time.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * FUNCTION: extractExpiration
     * Purpose: Decodes the token to read the 'exp' (expiration) field.
     */
    public Date extractExpiration(String token) {
        return (Jwts.parserBuilder()
                .setSigningKey(key) // We need the key to "unlock" and read the token
                .build()
                .parseClaimsJws(token)
                .getBody())
                .getExpiration();
    }

    /**
     * FUNCTION: extractEmail
     * Purpose: Decodes the token to read the 'sub' (subject/email) field.
     */
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}