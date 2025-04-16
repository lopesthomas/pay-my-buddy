package com.paymybuddy.pay_my_buddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.pay_my_buddy.model.Transaction;
import com.paymybuddy.pay_my_buddy.service.TransactionService;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public List<Transaction> getAllTransactions(){
        return transactionService.getAllTransactions();
    }

    @GetMapping("/email") // email in JSON body
    public List<Transaction> getTransactionsByEmail(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        String email = userDetails.getUsername();
        return transactionService.getTransactionsByUserEmail(email);
    }
}
