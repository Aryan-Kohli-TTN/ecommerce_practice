package com.bootcamp.service.impl;

import com.bootcamp.enums.TokenType;
import com.bootcamp.service.JWTService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;


@Component
public class JWTServiceImpl implements JWTService {
    @Value(value = "${jwt.token.secret}")
    public  String SECRET;
    @Value(value = "${jwt.access.token.secret}")
    public String ACCESS_SECRET;

    @Override
    public String generateToken(String email, TokenType tokenType, Date date) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email,tokenType,date);
    }
    @Override
    public String generateAccessToken(String email, TokenType tokenType, Date date) {
        Map<String, Object> claims = new HashMap<>();
        return createAccessToken(claims, email,tokenType,date);
    }

    private String createToken(Map<String, Object> claims, String email, TokenType tokenType,Date date) {
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(email)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + tokenType.getTokenTime()))
                .signWith(getSignKey())
                .compact();
    }
    private String createAccessToken(Map<String, Object> claims, String email, TokenType tokenType,Date date) {
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(email)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + tokenType.getTokenTime()))
                .signWith(getSignKeyAccess())
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Key getSignKeyAccess() {
        byte[] keyBytes = Decoders.BASE64.decode(ACCESS_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    @Override
    public String extractUsernameAccess(String token) {
        return extractClaimAccess(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    @Override
    public Date extractExpirationAccess(String token) {
        return extractClaimAccess(token, Claims::getExpiration);
    }
    @Override
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }
    @Override
    public Date extractIssuedAtAccess(String token) {
        return extractClaimAccess(token, Claims::getIssuedAt);
    }
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    @Override
    public <T> T extractClaimAccess(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaimsAccess(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    @Override
    public Claims extractAllClaimsAccess(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKeyAccess())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    @Override
    public Boolean isTokenExpiredAccess(String token) {
        return extractExpirationAccess(token).before(new Date());
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    @Override
    public Boolean validateTokenAccess(String token, UserDetails userDetails) {
        final String username = extractUsernameAccess(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpiredAccess(token));
    }
}
