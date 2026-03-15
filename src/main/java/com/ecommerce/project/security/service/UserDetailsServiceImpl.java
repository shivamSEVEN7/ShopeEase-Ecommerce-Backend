package com.ecommerce.project.security.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user =  userRepo.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Username not found"));
       return new UserDetailsImpl(user);
    }
}
