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

/**
 * Spring MVC controller for managing user-related actions.
 * Handles user registration, profile display, and profile updates.
 */
@Log
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Displays the registration form for new users.
     *
     * @param model the model used to pass a new {@link AppUser} instance to the view
     * @return the name of the Thymeleaf view (register.html)
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "register";
    }

    /**
     * Processes the registration form submission.
     * Attempts to save the new user using the {@link UserService}.
     *
     * @param user the user object populated from the form
     * @param model the model used to show potential error messages
     * @return a redirect to the login page if successful, or the registration page with errors
     */
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

    /**
     * Displays the authenticated user's profile.
     *
     * @param userDetails the currently authenticated Spring Security user
     * @param model the model used to pass user data to the view
     * @param edit optional flag to enable edit mode
     * @return the name of the Thymeleaf view (profile.html)
     */
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, Model model, @RequestParam(required = false) Boolean edit) {
        AppUser user = userRepository.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("edit", Boolean.TRUE.equals(edit));
        return "profile";
    }

    /**
     * Handles profile update submissions.
     *
     * @param userDetails the currently authenticated Spring Security user
     * @param user the updated user object from the form
     * @param redirectAttributes used to pass success or error messages via redirect
     * @return a redirect to logout on success, or back to the profile page with an error
     */
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
