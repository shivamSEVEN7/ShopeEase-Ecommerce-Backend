package com.ecommerce.project.security.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Optional<User> user;
        if (identifier.contains("@")) {
            user = userRepo.findByEmail(identifier);
        } else if (identifier.matches("\\d+")) {
            user = userRepo.findByMobileNumber(identifier);
        } else {
            user = userRepo.findByUsername(identifier);
        }

        if (user.isPresent()) {
            return new UserDetailsImpl(user.get());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
