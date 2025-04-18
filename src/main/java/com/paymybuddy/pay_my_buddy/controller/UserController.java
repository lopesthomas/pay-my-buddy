package com.paymybuddy.pay_my_buddy.controller;

import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.pay_my_buddy.model.AppUser;
import com.paymybuddy.pay_my_buddy.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // @GetMapping("/login")
    // public String getMethodName(@RequestParam String param) {
    //     return new String();
    // }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@RequestBody AppUser user) {
        try {
            userService.saveUser(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("User registered successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfileUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @RequestBody AppUser uptadeUserInfos) {
        //TODO: process PUT request
        String authEmail = userDetails.getUsername();
        try {
            userService.updateUser(authEmail, uptadeUserInfos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user: " + e.getMessage());
        }
        return ResponseEntity.ok("User updated resquested");
    }
    
}
