package com.bootcamp.config;

import com.bootcamp.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter){
        this.jwtFilter=jwtFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())) // Enable CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/seller/register",
                                "/api/customer/register",
                                "/api/customer/activate/account",
                                "/api/customer/new/activate/mail",
                                "/api/auth/login",
                                "/api/auth/refresh-token",
                                "/api/auth/update/password",
                                "/api/auth/new/forgot-password/mail",
                                "/api/image/get/**",
                                "/api/image/product-variation/primary/**",
                                "/api/image/product-variation/secondary/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/actuator",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
/*
  private static final List<String> EXCLUDED_URLS = List.of(
            "/api/seller/auth/**",
            "/api/seller/auth/register",
            "/api/customer/auth/register",
            "/api/seller/auth/login",
            "/api/customer/auth/login",
            "/api/customer/activate/account",
            "/api/customer/new/activate/mail",
            "/api/customer/refresh-token",
            "/api/seller/refresh-token",
            "/api/customer/logout",
            "/api/customer/new/forgot-password/mail",
            "/api/customer/update/password"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return EXCLUDED_URLS.stream().anyMatch(path::startsWith);
    }
* */