package com.paymybuddy.pay_my_buddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.pay_my_buddy.DTO.ConnectionDTO;
import com.paymybuddy.pay_my_buddy.service.ConnectionsService;

import lombok.extern.java.Log;

/**
 * Controller responsible for managing user connections (relations).
 * Provides endpoints to display the connection form and to handle new connection submissions.
 */
@Log
@Controller
public class ConnectionsController {

    @Autowired
    private ConnectionsService connectionsService;

    /**
     * Displays the connection(relation) form for the authenticated user.
     * Ensures that the model contains a ConnectionDTO object to bind the form data.
     *
     * @param model the model used to pass form data to the view
     * @return the name of the Thymeleaf view (relation.html)
     */
    @GetMapping("/relation")
    public String getFormConnection(Model model) {
        if (!model.containsAttribute("connection")){
            model.addAttribute("connection", new ConnectionDTO());
        }
            return "relation";
    }

    /**
     * Handles the submission of a new connection (relation) by the authenticated user.
     *
     * @param principal the authenticated user
     * @param sendedConnection the connection data submitted via the form
     * @param redirectAttributes used to pass success or error messages to the redirected view
     * @return a redirect to the /relation page with flash messages indicating result
     */
    @PostMapping("/relation")
    public String postRelation(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal, @ModelAttribute("connection") ConnectionDTO sendedConnection, RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getUsername();
            connectionsService.saveConnection(sendedConnection, email);
            redirectAttributes.addFlashAttribute("success", "Relation ajoutée avec succès");
        } catch (Exception e) {
            log.warning("Error register relation: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout de la relation");
        }
        return "redirect:/relation";
    } 
}
