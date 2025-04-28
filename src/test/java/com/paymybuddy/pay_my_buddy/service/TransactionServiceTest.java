package com.paymybuddy.pay_my_buddy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.paymybuddy.pay_my_buddy.DTO.TransactionDTO;
import com.paymybuddy.pay_my_buddy.model.AppUser;
import com.paymybuddy.pay_my_buddy.model.BankAccount;
import com.paymybuddy.pay_my_buddy.model.Transaction;
import com.paymybuddy.pay_my_buddy.repository.BankAccountRepository;
import com.paymybuddy.pay_my_buddy.repository.TransactionRepository;
import com.paymybuddy.pay_my_buddy.repository.UserRepository;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    private AppUser sender;
    private AppUser receiver;
    private BankAccount senderAccount;
    private BankAccount receiverAccount;
    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = new AppUser();
        sender.setEmail("sender@example.com");
        sender.setUsername("Sender User");

        receiver = new AppUser();
        receiver.setEmail("receiver@example.com");
        receiver.setUsername("Receiver User");

        senderAccount = new BankAccount();
        senderAccount.setUserId(sender);
        senderAccount.setBalance(new BigDecimal("100.00"));

        receiverAccount = new BankAccount();
        receiverAccount.setUserId(receiver);
        receiverAccount.setBalance(new BigDecimal("50.00"));

        transactionDTO = new TransactionDTO();
        transactionDTO.setSenderEmail("sender@example.com");
        transactionDTO.setReceiverEmail("receiver@example.com");
        transactionDTO.setAmount(new BigDecimal("20.00"));
        transactionDTO.setDescription("Payment for services");
    }

    @Test
    void testSaveTransaction_validTransaction_savesTransaction() {
        
        when(userRepository.findByEmail("sender@example.com")).thenReturn(sender);
        when(userRepository.findByEmail("receiver@example.com")).thenReturn(receiver);
        when(bankAccountRepository.findByUserId(sender)).thenReturn(Optional.of(senderAccount));
        when(bankAccountRepository.findByUserId(receiver)).thenReturn(Optional.of(receiverAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        BigDecimal initialSenderBalance = senderAccount.getBalance();
        BigDecimal initialReceiverBalance = receiverAccount.getBalance();

        Transaction savedTransaction = transactionService.saveTransaction(transactionDTO, "sender@example.com");

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        assertNotNull(savedTransaction);

        BigDecimal expectedSenderBalance = initialSenderBalance.subtract(transactionDTO.getAmount());
        assertEquals(expectedSenderBalance, senderAccount.getBalance(), "Sender's balance is not correct after transaction");
    
        BigDecimal expectedReceiverBalance = initialReceiverBalance.add(transactionDTO.getAmount());
        assertEquals(expectedReceiverBalance, receiverAccount.getBalance(), "Receiver's balance is not correct after transaction");
    }

    @Test
    void testSaveTransaction_senderAndReceiverSame_throwsException() {
        transactionDTO.setReceiverEmail("sender@example.com");
        when( userRepository.findByEmail("sender@example.com")).thenReturn(sender);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            transactionService.saveTransaction(transactionDTO, "sender@example.com");
        });

        assertEquals("Sender and receiver cannot be the same user", thrown.getMessage());
    }

    @Test
    void testSaveTransaction_senderNotFound_throwsException() {
        
        when(userRepository.findByEmail("sender@example.com")).thenReturn(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            transactionService.saveTransaction(transactionDTO, "sender@example.com");
        });

        assertEquals("Sender with email sender@example.com not found", thrown.getMessage());
    }

    @Test
    void testSaveTransaction_receiverNotFound_throwsException() {
        
        when( userRepository.findByEmail("sender@example.com")).thenReturn(sender);
        when(userRepository.findByEmail("receiver@example.com")).thenReturn(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            transactionService.saveTransaction(transactionDTO, "sender@example.com");
        });

        assertEquals("Receiver with email receiver@example.com not found", thrown.getMessage());
    }

    @Test
    void testSaveTransaction_insufficientBalance_throwsException() {
        
        when( userRepository.findByEmail("sender@example.com")).thenReturn(sender);
        when( userRepository.findByEmail("receiver@example.com")).thenReturn(receiver);
        when(bankAccountRepository.findByUserId(sender)).thenReturn(Optional.of(senderAccount));
        when(bankAccountRepository.findByUserId(receiver)).thenReturn(Optional.of(receiverAccount));
        transactionDTO.setAmount(new BigDecimal("200.00"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            transactionService.saveTransaction(transactionDTO, "sender@example.com");
        });

        assertEquals("Insufficient balance", thrown.getMessage());
    }

    @Test
    void testSaveTransaction_nullEmail_throwsException() {
        
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            transactionService.saveTransaction(transactionDTO, null);
        });

        assertEquals("Authentication email is null or empty", thrown.getMessage());
    }

    @Test
    void testGetTransactionsByUserEmail_validEmail_returnsTransactions() {
        
        Transaction transaction = new Transaction();
        transaction.setSender(senderAccount);
        transaction.setReceiver(receiverAccount);
        transaction.setAmount(new BigDecimal("20.00"));
        transaction.setDescription("Payment for services");
        transaction.setTransactionDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        when(userRepository.findByEmail("sender@example.com")).thenReturn(sender);
        when(transactionRepository.findBySender_UserId_Id(sender.getId())).thenReturn(List.of(transaction));

        List<TransactionDTO> transactions = transactionService.getTransactionsByUserEmail("sender@example.com");

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals("sender@example.com", transactions.get(0).getSenderEmail());
        assertEquals("receiver@example.com", transactions.get(0).getReceiverEmail());
    }

    @Test
    void testGetTransactionsByUserEmail_invalidEmail_throwsException() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            transactionService.getTransactionsByUserEmail("");
        });

        assertEquals("Email is null or empty", thrown.getMessage());
    }
}
