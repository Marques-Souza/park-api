package com.marquesdev.park_api.jwt;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.UUID;

public class JwtUserDetails extends User {

    private com.marquesdev.park_api.entity.User user;

    public JwtUserDetails(com.marquesdev.park_api.entity.User user) {
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public UUID getId(){
        return this.user.getId();
    }

    public String getRole(){
        return this.user.getRole().name();
    }
}
