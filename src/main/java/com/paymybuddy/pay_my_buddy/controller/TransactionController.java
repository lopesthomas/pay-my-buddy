package com.paymybuddy.pay_my_buddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.pay_my_buddy.DTO.ConnectionDTO;
import com.paymybuddy.pay_my_buddy.DTO.TransactionDTO;
import com.paymybuddy.pay_my_buddy.service.ConnectionsService;
import com.paymybuddy.pay_my_buddy.service.TransactionService;

import lombok.extern.java.Log;

@Log
@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ConnectionsService connectionsService;

    @GetMapping("/transaction")
    public String showTransactionsAndForm(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal ,Model model) {
        String email = principal.getUsername();
        List<ConnectionDTO> connections = connectionsService.getConnectionsList(email);
        List<TransactionDTO> transactions = transactionService.getTransactionsByUserEmail(email);
        model.addAttribute("connections", connections);
        model.addAttribute("transactions", transactions);
        if (!model.containsAttribute("transaction")) {
            model.addAttribute("transaction", new TransactionDTO());
        }
        return "transaction";
    }

    @PostMapping("/transaction")
    public String sendTransaction(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal, @ModelAttribute("transaction") TransactionDTO sendedTransaction, RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getUsername();
            transactionService.saveTransaction(sendedTransaction, email);
            redirectAttributes.addFlashAttribute("success", "Transaction envoyée avec succès");
        } catch (Exception e) {
            log.warning("Error register transaction: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'envoi de la transaction");
        }
        return "redirect:/transaction";
    }
}
