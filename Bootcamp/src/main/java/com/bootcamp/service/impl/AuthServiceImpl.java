package com.bootcamp.service.impl;

import com.bootcamp.co.AuthRequest;
import com.bootcamp.enums.TokenType;
import com.bootcamp.exception.auth.*;
import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.response.TokenResponse;
import com.bootcamp.service.AuthService;
import com.bootcamp.service.BlackListedTokenService;
import com.bootcamp.service.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Locale;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final BlackListedTokenService blackListedTokenService;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public ApiResponse<Object> userLogin(AuthRequest authRequest, HttpServletResponse response) {
        logger.info("User login attempt for email: {}", authRequest.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                logger.info("User authenticated successfully for email: {}", authRequest.getEmail());

                String accessToken = jwtService.generateAccessToken(authRequest.getEmail(), TokenType.ACCESS_TOKEN, new Date());
                String refreshToken = jwtService.generateToken(authRequest.getEmail(), TokenType.REFRESH_TOKEN, new Date());

                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true).secure(true).path("/")
                        .maxAge(24 * 60 * 60).sameSite("Lax").build();
                response.addHeader("Set-Cookie", refreshCookie.toString());

                logger.info("Refresh token issued for email: {}", authRequest.getEmail());
                return ResponseUtil.withStatus(HttpStatus.OK, new TokenResponse(accessToken));
            }
        } catch (UsernameNotFoundException e) {
            logger.error("User not found for email: {}", authRequest.getEmail());
            throw new UsernameNotFoundException("User not found");
        } catch (BadCredentialsException exception) {
            logger.error("Incorrect password for email: {}", authRequest.getEmail());
            throw new BadCredentialsException("Incorrect Password");
        } catch (UserNotActiveException e) {
            logger.error("User not active for email: {}", authRequest.getEmail());
            throw new UserNotActiveException("User not active");
        } catch (Exception e) {
            logger.error("Unexpected error during login for email: {}", authRequest.getEmail());
            throw new RuntimeException(e);
        }

        logger.error("Server error during login for email: {}", authRequest.getEmail());
        return ResponseUtil.errorStatus(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER ERROR");
    }

    @Override
    public ApiResponse<Object> refreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        if (refreshToken == null) {
            logger.error("Refresh token is null, user logged out.");
            throw new UserLoggedOutException();
        }

        try {
            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(refreshToken, userDetails) && !blackListedTokenService.isTokenBlackListed(refreshToken)) {
                logger.info("Valid refresh token for user: {}", username);

                blackListToken(refreshToken, jwtService.extractExpiration(refreshToken));
                String newAccessToken = jwtService.generateAccessToken(username, TokenType.ACCESS_TOKEN, new Date());
                String newRefreshToken = jwtService.generateToken(username, TokenType.REFRESH_TOKEN, new Date());

                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                        .httpOnly(true).secure(true).path("/").maxAge(24 * 60 * 60).sameSite("Lax").build();
                response.addHeader("Set-Cookie", refreshCookie.toString());

                logger.info("New refresh token issued for user: {}", username);
                return ResponseUtil.withStatus(HttpStatus.OK, new TokenResponse(newAccessToken));
            } else {
                logger.error("Invalid refresh token for user: {}", username);
                throw new InvalidRefreshTokenException();
            }
        } catch (UsernameNotFoundException e) {
            logger.error("User not found during refresh token request");
            throw new UsernameNotFoundException("User not found");
        } catch (ExpiredJwtException e) {
            logger.error("Refresh token expired for user: {}", refreshToken);
            throw new RefreshTokenExpiredException();
        } catch (Exception e) {
            logger.error("Error during refresh token process for token: {} {}", refreshToken,e.getMessage());
            throw new InvalidRefreshTokenException();
        }
    }

    @Override
    public ApiResponse<Object> logoutUser(String refreshToken, HttpServletResponse response, HttpServletRequest request) {
        if (refreshToken == null) {
            logger.error("Refresh token is null, user logged out.");
            throw new UserLoggedOutException();
        }

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(refreshToken, userDetails) && !blackListedTokenService.isTokenBlackListed(refreshToken)) {
                logger.info("Valid refresh token for user: {}", username);

                String authHeader = request.getHeader("Authorization");
                String accessToken = authHeader.substring(7);

                blackListToken(accessToken, jwtService.extractExpirationAccess(accessToken));
                blackListToken(refreshToken, jwtService.extractExpiration(refreshToken));

                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                        .httpOnly(true).secure(true).path("/").maxAge(0).sameSite("Lax").build();
                response.addHeader("Set-Cookie", refreshCookie.toString());

                logger.info("User logged out successfully: {}", username);

                Locale locale = LocaleContextHolder.getLocale();
                return ResponseUtil.ok(messageSource.getMessage("message.user.logged.out", null, locale));
            } else {
                logger.error("Invalid refresh token for user: {}", username);
                throw new InvalidJwtTokenException("Invalid Refresh Token");
            }
        } catch (UsernameNotFoundException e) {
            logger.error("User not found during logout");
            throw new UsernameNotFoundException("User not found");
        } catch (ExpiredJwtException e) {
            logger.error("Refresh token expired during logout for user: {}", refreshToken);
            throw new RefreshTokenExpiredException();
        } catch (Exception e) {
            logger.error("Error during logout for user: {}", refreshToken);
            throw new InvalidJwtTokenException("Invalid Refresh Token");
        }
    }

    private void blackListToken(String token, Date tokenExpiryTime) {
        blackListedTokenService.blackListToken(token, tokenExpiryTime);
    }



}
