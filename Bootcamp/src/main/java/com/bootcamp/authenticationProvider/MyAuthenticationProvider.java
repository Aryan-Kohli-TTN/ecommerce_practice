package com.bootcamp.authenticationProvider;

import com.bootcamp.exception.auth.UserIsLockedException;
import com.bootcamp.exception.auth.UserNotActiveException;
import com.bootcamp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MyAuthenticationProvider implements AuthenticationProvider {

//    @Autowired
//    UserDetailsService userDetailsService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserService userService;
    public Authentication authenticate(Authentication authentication) throws AuthenticationException{
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
//        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        UserDetails userDetails = userService.loadUserByUsername(username);
        if(userDetails==null)
            throw new UsernameNotFoundException("User not found");
        if(!userDetails.isEnabled())
            throw new UserNotActiveException("User Not Active");
        if(!userDetails.isAccountNonLocked())
            throw new UserIsLockedException("User is locked");
        if(!userDetails.isCredentialsNonExpired())
            throw new CredentialsExpiredException("Credential Expired");
        if(passwordEncoder.matches(password,userDetails.getPassword())){
            userService.setPasswordCountZero(userDetails);
            return new UsernamePasswordAuthenticationToken(authentication.getName(),null,userDetails.getAuthorities());
        }
        else{
           userService.increaseCountPassword(userDetails);
            throw new BadCredentialsException("Password does not match");
        }
    }

    public boolean supports(Class<?> authentication){
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
