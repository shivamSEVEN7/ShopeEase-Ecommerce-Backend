package com.ecommerce.project.security.service;

import com.ecommerce.project.model.Permissions;
import com.ecommerce.project.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    User user;
    public UserDetailsImpl(User user){
        this.user = user;
    }
    public Set<Permissions> permi = new HashSet<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        Set<SimpleGrantedAuthority> roles= user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName())).collect(Collectors.toSet());
        user.getRoles().forEach(role -> {
           permi= role.getRoleName().getPermissions();
        });
        permi.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.name())));
        authorities.addAll(roles);


        return authorities;
    }
    public long getUserId() {
        return user.getUserId();
    }
    public String getEmail() {
        return user.getEmail();
    }
    public User getUser() {return this.user;}
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
