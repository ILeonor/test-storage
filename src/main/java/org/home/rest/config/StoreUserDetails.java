package org.home.rest.config;

import org.home.repository.model.StoreUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class StoreUserDetails implements UserDetails {
    public final StoreUser              storeUser;
    private      List<GrantedAuthority> authorities;

    public StoreUserDetails(StoreUser user) {
        this.storeUser = user;

        this.authorities = new LinkedList<>();
        this.authorities.add(new SimpleGrantedAuthority("USER"));
        if (user.getIsAdmin()) {
            this.authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return storeUser.getPassword();
    }

    @Override
    public String getUsername() {
        return storeUser.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
