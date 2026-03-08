package com.bootcamp.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_CUSTOMER,
    ROLE_SELLER;
    @Override
    public String getAuthority() {
        return name();
    }
}
