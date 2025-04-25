package com.paymybuddy.pay_my_buddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.paymybuddy.pay_my_buddy.model.AppUser;
import com.paymybuddy.pay_my_buddy.service.UserService;

import lombok.extern.java.Log;

@Log
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerNewUser(@ModelAttribute("user") AppUser user, Model model) {
        try {
            userService.saveUser(user);
        } catch (Exception e) {
            log.warning("Error register user: " + e.getMessage());
            model.addAttribute("error" , "Error registering user");
            
            return "register";
        }
        return "redirect:/login";
    }

    

    @PutMapping("/update")
    public ResponseEntity<?> updateProfileUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @RequestBody AppUser uptadeUserInfos) {
        String authEmail = userDetails.getUsername();
        try {
            userService.updateUser(authEmail, uptadeUserInfos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user: " + e.getMessage());
        }
        return ResponseEntity.ok("User updated resquested");
    }
    
}
