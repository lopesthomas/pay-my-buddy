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

@Log
@Controller
public class ConnectionsController {

    @Autowired
    private ConnectionsService connectionsService;

    @GetMapping("/relation")
    public String getFormConnection(Model model) {
        if (!model.containsAttribute("connection")){
            model.addAttribute("connection", new ConnectionDTO());
        }
            return "relation";
    }

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
