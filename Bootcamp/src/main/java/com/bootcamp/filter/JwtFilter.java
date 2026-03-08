package com.bootcamp.filter;

import com.bootcamp.response.ApiResponse;
import com.bootcamp.response.ResponseUtil;
import com.bootcamp.service.BlackListedTokenService;
import com.bootcamp.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class JwtFilter extends OncePerRequestFilter {

    JWTService jwtService;
    UserDetailsService userDetailsService;
    BlackListedTokenService blackListedTokenService;
    @Autowired
    public JwtFilter(JWTService jwtService, UserDetailsService userDetailsService,BlackListedTokenService blackListedTokenService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.blackListedTokenService=blackListedTokenService;
    }

    private static final List<String> EXCLUDED_URLS = List.of(
            "/api/seller/register",
            "/api/customer/register",
            "/api/customer/activate/account",
            "/api/customer/new/activate/mail",
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/api/auth/update/password",
            "/api/auth/new/forgot-password/mail",
            "/api/image/get/",
            "/api/image/product-variation/primary/",
            "/api/image/product-variation/secondary/",
            "/swagger-ui.html",
            "/swagger-ui/",
            "/v3/api-docs",
            "/v3/api-docs/",
            "/actuator",
            "/actuator/"
    );
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return EXCLUDED_URLS.stream().anyMatch(path::startsWith);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String header = request.getHeader("Authorization");
            try {
                if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String token = header.substring(7);
                    String username = jwtService.extractUsernameAccess(token);

                    if(username!=null){
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if(userDetails==null)
                        {
                            handleException("User not Found",response,HttpStatus.NOT_FOUND);
                            return;
                        }
                        if(blackListedTokenService.isTokenBlackListed(token))
                        {
                           handleException("token is blacklisted",response,HttpStatus.UNAUTHORIZED);
                           return;
                        }
                        if(jwtService.validateTokenAccess(token,userDetails)){
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                            filterChain.doFilter(request,response);

                        }
                        else {
                            handleException("Invalid token ",response,HttpStatus.UNAUTHORIZED);
                        }
                    }
                    else
                    {
                        handleException("Invalid token ", response,HttpStatus.BAD_REQUEST);
                    }

                }
                else {
                    handleException("token not available ",response,HttpStatus.UNAUTHORIZED);
                }
            } catch (UsernameNotFoundException e) {
               handleException("Username Not found token",response,HttpStatus.NOT_FOUND);
            } catch (ExpiredJwtException e) {
               handleException("Expired token",response,HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {

                handleException("Invalid token", response,HttpStatus.UNAUTHORIZED);
            }
    }
    private void handleException(String message,HttpServletResponse httpServletResponse,HttpStatus httpStatus) throws IOException {
        httpServletResponse.setStatus(httpStatus.value());
        ApiResponse<Object> apiResponse = ResponseUtil.errorStatus(httpStatus,message);
        ObjectMapper objectMapper = new ObjectMapper();
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
