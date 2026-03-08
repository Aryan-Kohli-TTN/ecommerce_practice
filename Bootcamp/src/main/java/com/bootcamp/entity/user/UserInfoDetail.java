package com.bootcamp.entity.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoDetail implements UserDetails {
    String username;
    String password;
    List<GrantedAuthority>authorities;

    boolean active = false;
    boolean locked = false;
    boolean expired = false;

    public UserInfoDetail(String username, String password,boolean active,boolean locked,List<GrantedAuthority> authorities,boolean expired) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.active=active;
        this.locked=locked;
        this.expired=expired;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !expired;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
