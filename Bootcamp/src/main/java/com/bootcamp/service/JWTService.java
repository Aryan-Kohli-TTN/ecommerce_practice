package com.bootcamp.service;

import com.bootcamp.enums.TokenType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JWTService {
    String generateToken(String email, TokenType tokenType, Date date);

    String generateAccessToken(String email, TokenType tokenType, Date date);

    String extractUsername(String token);

    String extractUsernameAccess(String token);

    Date extractExpiration(String token);

    Date extractExpirationAccess(String token);

    Date extractIssuedAt(String token);

    Date extractIssuedAtAccess(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    <T> T extractClaimAccess(String token, Function<Claims, T> claimsResolver);

    Claims extractAllClaims(String token);

    Claims extractAllClaimsAccess(String token);

    Boolean isTokenExpired(String token);

    Boolean isTokenExpiredAccess(String token);

    Boolean validateToken(String token, UserDetails userDetails);

    Boolean validateTokenAccess(String token, UserDetails userDetails);
}
