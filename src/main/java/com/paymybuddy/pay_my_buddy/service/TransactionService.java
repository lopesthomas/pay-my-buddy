package com.paymybuddy.pay_my_buddy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.pay_my_buddy.model.Transaction;
import com.paymybuddy.pay_my_buddy.repository.BankAccountRepository;
import com.paymybuddy.pay_my_buddy.repository.TransactionRepository;
import com.paymybuddy.pay_my_buddy.repository.UserRepository;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByUserEmail(String email) {
        Long userId = userRepository.findByEmail(email).getId();
        System.out.println(email + " " + userId);

        return transactionRepository.findAll().stream()
                .filter(tr -> tr.getSender().getIdbank_account().equals(userId))
                .toList();
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findAll().stream()
                .filter(tr -> tr.getSender().equals(userId))
                .toList();
    }

}
