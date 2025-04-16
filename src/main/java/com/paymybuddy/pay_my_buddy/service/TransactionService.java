package com.paymybuddy.pay_my_buddy.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.pay_my_buddy.DTO.TransactionDTO;
import com.paymybuddy.pay_my_buddy.model.AppUser;
import com.paymybuddy.pay_my_buddy.model.BankAccount;
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
                .filter(tr -> tr.getSender().getUserId().equals(userId))
                .toList();
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findAll().stream()
                .filter(tr -> tr.getSender().equals(userId))
                .toList();
    }

    public Transaction saveTransaction(TransactionDTO transaction, String authEmail) {
        try {
            AppUser senderUser = userRepository.findByEmail(authEmail);
            if (senderUser == null) {
                throw new RuntimeException("Sender with email " + transaction.getSenderEmail() + " not found");
            }

            AppUser receiverUser = userRepository.findByEmail(transaction.getReceiverEmail());
            if (receiverUser == null) {
                throw new RuntimeException("Receiver with email " + transaction.getReceiverEmail() + " not found");
            }

            Long senderId = senderUser.getId();
            Long receiverId = receiverUser.getId();

            System.out.println("senderId " + senderId);
            System.out.println("receiverId " + receiverId);

            BankAccount senderAccount = bankAccountRepository.findByUserId(senderUser)
                .orElseThrow(() -> new RuntimeException("Sender bank account not found for userId: " + senderId));

            BankAccount receiverAccount = bankAccountRepository.findByUserId(receiverUser)
                .orElseThrow(() -> new RuntimeException("Receiver bank account not found for userId: " + receiverId));

            Transaction newTransaction = new Transaction();
            newTransaction.setSender(senderAccount);
            newTransaction.setReceiver(receiverAccount);
            newTransaction.setAmount(transaction.getAmount());
            newTransaction.setDescription(transaction.getDescription());
            newTransaction.setTransaction_date(java.sql.Timestamp.valueOf(LocalDateTime.now()));
    
            return transactionRepository.save(newTransaction);
        } catch (Exception e) {
            System.out.println("Erreur lors de l'enregistrement de la transaction : " + e.getMessage());
            throw e;
        }
    }

}
