package com.paymybuddy.pay_my_buddy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.paymybuddy.pay_my_buddy.model.AppUser;
import com.paymybuddy.pay_my_buddy.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        System.out.println("User found: " + user.getEmail());
        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}

