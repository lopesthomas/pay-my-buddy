package com.paymybuddy.pay_my_buddy.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.pay_my_buddy.PayMyBuddyApplication;
import com.paymybuddy.pay_my_buddy.DTO.TransactionDTO;
import com.paymybuddy.pay_my_buddy.service.ConnectionsService;
import com.paymybuddy.pay_my_buddy.service.TransactionService;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = PayMyBuddyApplication.class)
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private ConnectionsService connectionsService;

    @Test
    @WithMockUser(username = "test@example.com")
    public void testShowTransactionsAndForm() throws Exception {
        // Simuler retour vide
        Mockito.when(connectionsService.getConnectionsList("test@example.com"))
                .thenReturn(Collections.emptyList());
        Mockito.when(transactionService.getTransactionsByUserEmail("test@example.com"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/transaction"))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("connections"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attributeExists("transaction"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testSendTransactionSuccess() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO();
        
        mockMvc.perform(post("/transaction")
                .flashAttr("transaction", transactionDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/transaction"))
                .andExpect(flash().attributeExists("success"));

        verify(transactionService, times(1)).saveTransaction(Mockito.any(TransactionDTO.class), Mockito.anyString());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testSendTransactionFail() throws Exception {
        doThrow(new RuntimeException("Erreur lors de l'envoi de la transaction"))
            .when(transactionService)
            .saveTransaction(Mockito.any(TransactionDTO.class), Mockito.anyString());

        mockMvc.perform(post("/transaction")
                .flashAttr("transaction", new TransactionDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/transaction"))
                .andExpect(flash().attributeExists("error"));

        verify(transactionService, times(1)).saveTransaction(Mockito.any(TransactionDTO.class), Mockito.anyString());
    }
}
