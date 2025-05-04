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

/**
 * Controller for managing user transactions.
 * Allows users to view their connections and transaction history,
 * and to submit new transactions to their contacts.
 */
@Log
@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ConnectionsService connectionsService;

    /**
     * Displays the transaction page, including the user's connections and past transactions.
     *
     * @param principal the authenticated user provided by Spring Security
     * @param model the model used to pass connection and transaction data to the view
     * @return the name of the Thymeleaf view (transaction.html)
     */
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

    /**
     * Handles the submission of a new transaction by the authenticated user.
     *
     * @param principal the authenticated user
     * @param sendedTransaction the transaction data submitted by the form
     * @param redirectAttributes used to pass success or error messages after redirect
     * @return a redirect to the transaction page with a success or error flash message
     */
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
