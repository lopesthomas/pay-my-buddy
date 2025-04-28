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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.pay_my_buddy.DTO.ConnectionDTO;
import com.paymybuddy.pay_my_buddy.PayMyBuddyApplication;
import com.paymybuddy.pay_my_buddy.service.ConnectionsService;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = PayMyBuddyApplication.class)
@AutoConfigureMockMvc
public class ConnectionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConnectionsService connectionsService;

    @Test
    public void testGetFormConnection() throws Exception {
        mockMvc.perform(get("/relation"))
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(model().attributeExists("connection"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testPostRelationSuccess() throws Exception {
        ConnectionDTO connectionDTO = new ConnectionDTO();

        mockMvc.perform(post("/relation")
                .flashAttr("connection", connectionDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/relation"))
                .andExpect(flash().attributeExists("success"));

        verify(connectionsService, times(1)).saveConnection(Mockito.any(ConnectionDTO.class), Mockito.anyString());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testPostRelationFail() throws Exception {
        doThrow(new RuntimeException("Erreur lors de l'ajout de la relation"))
            .when(connectionsService)
            .saveConnection(Mockito.any(ConnectionDTO.class), Mockito.anyString());

        mockMvc.perform(post("/relation")
                .flashAttr("connection", new ConnectionDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/relation"))
                .andExpect(flash().attributeExists("error"));

        verify(connectionsService, times(1)).saveConnection(Mockito.any(ConnectionDTO.class), Mockito.anyString());
    }
}