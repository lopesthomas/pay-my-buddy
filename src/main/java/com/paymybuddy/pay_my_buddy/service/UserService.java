package com.paymybuddy.pay_my_buddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.paymybuddy.pay_my_buddy.model.AppUser;
import com.paymybuddy.pay_my_buddy.repository.UserRepository;

import lombok.extern.java.Log;

@Log
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(AppUser user) {
        if (user.getUsername() == null || user.getUsername().isEmpty() || user.getUsername().contains(" ")) {
            throw new RuntimeException("Username is not valid, null or empty");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$") || userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email is null or empty or already taken");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("Password is null or empty");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUser(String authEmail, AppUser updateUser) {
        if(authEmail == null || authEmail.isEmpty()) {
            throw new RuntimeException("Auth email is null or empty");
        }

        Long userId = userRepository.findByEmail(authEmail).getId();
        if(userId == null) {
            throw new RuntimeException("User ID shearched by auth email is null");
        }
        updateUser.setId(userId);

        if (updateUser.getId() == null || updateUser.getId() == 0) {
            throw new RuntimeException("User ID is null");
        }
        AppUser existingUser = userRepository.findById(updateUser.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        if (updateUser.getUsername() != null && !updateUser.getUsername().isEmpty() && !updateUser.getUsername().equals(existingUser.getUsername()) && !updateUser.getUsername().contains(" ")) {
            existingUser.setUsername(updateUser.getUsername());
        } else if(updateUser.getUsername() == null || updateUser.getUsername().isEmpty() || updateUser.getUsername().equals(existingUser.getUsername())) {
            log.info("Username is the same or null or empty, not updated");
        } else {
            throw new RuntimeException("Username not valid");
        }
        
        if (updateUser.getEmail() != null && !updateUser.getEmail().isEmpty() && !updateUser.getEmail().equals(existingUser.getEmail()) && updateUser.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$") && userRepository.findByEmail(updateUser.getEmail()) == null) {
            existingUser.setEmail(updateUser.getEmail());
        } else if(updateUser.getEmail() == null || updateUser.getEmail().isEmpty() || updateUser.getEmail().equals(existingUser.getEmail())) {
            log.info("Email is the same or null, not updated");
        } else {
            throw new RuntimeException("Email not valid or already taken");
        }

        if (updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        } else if(updateUser.getPassword() == null || updateUser.getPassword().isEmpty()) {
            log.info("Password is null or empty, not updated");
        } else {
            throw new RuntimeException("Password not valid");
        }

        userRepository.save(existingUser);
    }
    

}
