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

import lombok.extern.java.Log;

@Service
@Log
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

    public List<TransactionDTO> getTransactionsByUserEmail(String email) {
        try {
            if(email == null || email.isEmpty()) {
                throw new RuntimeException("Email is null or empty");
            }
            Long userId = userRepository.findByEmail(email).getId();

            return transactionRepository.findBySender_UserId_Id(userId).stream()
                    .map(tr -> {
                        TransactionDTO transactionDTO = new TransactionDTO();
                        transactionDTO.setId(tr.getId());
                        transactionDTO.setSenderEmail(email);
                        transactionDTO.setReceiverEmail(tr.getReceiver().getUserId().getEmail());
                        transactionDTO.setAmount(tr.getAmount());
                        transactionDTO.setDescription(tr.getDescription());
                        transactionDTO.setTransactionDate(tr.getTransactionDate().toString());
                        return transactionDTO;
                    })
                    .toList();
        } catch (Exception e) {
            log.severe("Error retrieving transactions by user email: " + e.getMessage());
            throw e;
        }

    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findAll().stream()
                .filter(tr -> tr.getSender().getUserId().getId().equals(userId))
                .toList();
    }

    public Transaction saveTransaction(TransactionDTO transaction, String authEmail) {
        try {
            if(authEmail == null || authEmail.isEmpty()) {
                throw new RuntimeException("Authentication email is null or empty");
            }
            if(!authEmail.equals(transaction.getSenderEmail())) {
                throw new RuntimeException("Sender email does not match authenticated user email");
            }

            AppUser senderUser = userRepository.findByEmail(authEmail);
            if (senderUser == null) {
                throw new RuntimeException("Sender with email " + transaction.getSenderEmail() + " not found");
            }

            AppUser receiverUser = userRepository.findByEmail(transaction.getReceiverEmail());
            if (receiverUser == null) {
                throw new RuntimeException("Receiver with email " + transaction.getReceiverEmail() + " not found");
            }

            if (senderUser.getEmail().equals(receiverUser.getEmail())) {
                throw new RuntimeException("Sender and receiver cannot be the same user");
            }

            Long senderId = senderUser.getId();
            Long receiverId = receiverUser.getId();

            log.info("senderId " + senderId);
            log.info("receiverId " + receiverId);

            BankAccount senderAccount = bankAccountRepository.findByUserId(senderUser)
                .orElseThrow(() -> new RuntimeException("Sender bank account not found for userId: " + senderId));

            BankAccount receiverAccount = bankAccountRepository.findByUserId(receiverUser)
                .orElseThrow(() -> new RuntimeException("Receiver bank account not found for userId: " + receiverId));

            if (senderAccount.getBalance().compareTo(transaction.getAmount()) < 0 || senderAccount.getBalance() - transaction.getAmount() < 0) {
                throw new RuntimeException("Insufficient balance");
            }

            senderAccount.setBalance(senderAccount.getBalance() - (transaction.getAmount()));
            receiverAccount.setBalance(receiverAccount.getBalance() + (transaction.getAmount()));
            bankAccountRepository.save(senderAccount);
            bankAccountRepository.save(receiverAccount);

            Transaction newTransaction = new Transaction();
            newTransaction.setSender(senderAccount);
            newTransaction.setReceiver(receiverAccount);
            newTransaction.setAmount(transaction.getAmount());
            newTransaction.setDescription(transaction.getDescription());
            newTransaction.setTransactionDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));
    
            return transactionRepository.save(newTransaction);
        } catch (Exception e) {
            log.severe("Error saving transaction: " + e.getMessage());
            throw e;
        }
    }

}
