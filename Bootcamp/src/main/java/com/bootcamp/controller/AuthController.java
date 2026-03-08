package com.bootcamp.controller;

import com.bootcamp.co.AuthRequest;
import com.bootcamp.co.EmailCO;
import com.bootcamp.co.ForgotPasswordCO;
import com.bootcamp.service.AuthService;
import com.bootcamp.service.ForgotPasswordService;
import com.bootcamp.service.JWTService;
import com.bootcamp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final JWTService jwtService;
    private final UserService userService;
    private final AuthService authService;
    private final ForgotPasswordService forgotPasswordService;

    @GetMapping("/login")
    ResponseEntity<Object> userLogin(@Valid @RequestBody AuthRequest authRequest, HttpServletResponse response){
        return new ResponseEntity<>(authService.userLogin(authRequest,response), HttpStatus.OK);
    }
    @GetMapping("/refresh-token")
    public ResponseEntity<Object> refreshAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response, HttpServletRequest request) {
        return new ResponseEntity<>(authService.refreshToken(refreshToken, request,response),HttpStatus.OK);
    }
    @GetMapping("/logout")
    public ResponseEntity<Object> userLogout(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response, HttpServletRequest request) {
        return new ResponseEntity<>(authService.logoutUser(refreshToken,response,request),HttpStatus.OK);
    }
    @PostMapping("/new/forgot-password/mail")
    public ResponseEntity<Object> sendForgotPasswordMail(@Valid @RequestBody EmailCO emailCO){
        return new ResponseEntity<>(forgotPasswordService.forgotPasswordMailSend(emailCO.getEmail()),HttpStatus.OK);
    }

    @PutMapping("/update/password")
    public ResponseEntity<Object> updatePassword(@Valid @RequestBody ForgotPasswordCO forgotPasswordCO){
        return new ResponseEntity<>(forgotPasswordService.updateForgotPassword(forgotPasswordCO),HttpStatus.OK);
    }
}
