package com.paymybuddy.pay_my_buddy.service;

import java.math.BigDecimal;
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

import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

/**
 * Service class responsible for handling transactions between users,
 * including retrieval and creation of transaction records.
 */
@Service
@Log
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;

    /**
     * Retrieves a list of transactions made by a user based on their email.
     *
     * @param email the email of the sender
     * @return a list of TransactionDTOs representing the user's transactions
     * @throws RuntimeException if the email is invalid or an error occurs during processing
     */
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
                        transactionDTO.setSenderUsername(tr.getSender().getUserId().getUsername());
                        transactionDTO.setSenderEmail(email);
                        transactionDTO.setReceiverUsername(tr.getReceiver().getUserId().getUsername());
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

    /**
     * Saves a new transaction between two users after validating balances and account existence.
     * Performs a transfer from the sender's bank account to the receiver's.
     *
     * @param transaction the transaction data from the form
     * @param authEmail the email of the currently authenticated user
     * @return the persisted Transaction entity
     * @throws RuntimeException if validation fails (e.g., insufficient funds, invalid accounts)
     */
    @Transactional
    public Transaction saveTransaction(TransactionDTO transaction, String authEmail) {
        try {
            if(authEmail == null || authEmail.isEmpty()) {
                throw new RuntimeException("Authentication email is null or empty");
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

            if (senderAccount.getBalance().compareTo(transaction.getAmount()) <= 0 || senderAccount.getBalance().subtract(transaction.getAmount()).compareTo(new BigDecimal(0)) <= 0 || transaction.getAmount().compareTo(new BigDecimal(0)) <= 0) {
                throw new RuntimeException("Insufficient balance");
            }

            senderAccount.setBalance(senderAccount.getBalance().subtract(transaction.getAmount()));
            receiverAccount.setBalance(receiverAccount.getBalance().add(transaction.getAmount()));
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
