package com.paymybuddy.pay_my_buddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.pay_my_buddy.model.AppUser;
import com.paymybuddy.pay_my_buddy.repository.UserRepository;
import com.paymybuddy.pay_my_buddy.service.UserService;

import lombok.extern.java.Log;


@Log
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, Model model, @RequestParam(required = false) Boolean edit) {
        AppUser user = userRepository.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("edit", Boolean.TRUE.equals(edit));
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @ModelAttribute("user") AppUser user, RedirectAttributes redirectAttributes) {
        String authEmail = userDetails.getUsername();
        try {
            userService.updateUser(authEmail, user);
            redirectAttributes.addFlashAttribute("success", "Utilisateur modifié avec succès");
        } catch (Exception e) {
            log.warning("Error updating user: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur de la modification de l'utilisateur");
            return "redirect:/profile?edit=true";
        }
        return "redirect:/logout";
    }
}
