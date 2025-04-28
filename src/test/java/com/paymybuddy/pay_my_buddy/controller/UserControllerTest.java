package com.paymybuddy.pay_my_buddy.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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

import com.paymybuddy.pay_my_buddy.PayMyBuddyApplication;
import com.paymybuddy.pay_my_buddy.model.AppUser;
import com.paymybuddy.pay_my_buddy.repository.UserRepository;
import com.paymybuddy.pay_my_buddy.service.UserService;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.MOCK,
  classes = PayMyBuddyApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    public void testShowRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    public void testRegisterNewUserSuccess() throws Exception {
        AppUser user = new AppUser();
        user.setEmail("test@example.com");
        mockMvc.perform(post("/register")
                .param("username", "test@example.com")
                .param("email", "test@example.com")
                .param("password", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        
        verify(userService, times(1)).saveUser(Mockito.any(AppUser.class));
    }

    @Test
    public void testRegisterFail() throws Exception {
        doThrow(new RuntimeException("Erreur lors de l'enregistrement"))
        .when(userService)
        .saveUser(org.mockito.ArgumentMatchers.any());

        mockMvc.perform(post("/register")
                .param("email", " ")
        )
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testShowProfile() throws Exception {
        AppUser user = new AppUser();
        user.setEmail("test@example.com");

        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testUpdateProfileSuccess() throws Exception {
        AppUser user = new AppUser();
        user.setEmail("test@example.com");
        
        mockMvc.perform(post("/profile")
                .flashAttr("user", user)) // Simuler l'envoi de l'objet user en POST
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logout"))
                .andExpect(flash().attributeExists("success"));

        verify(userService, times(1)).updateUser(Mockito.anyString(), Mockito.any(AppUser.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testUpdateProfileFail() throws Exception {
        doThrow(new RuntimeException("Erreur lors de la mise à jour de l'utilisateur"))
                .when(userService)
                .updateUser(Mockito.anyString(), Mockito.any(AppUser.class));

        mockMvc.perform(post("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/profile?edit=true"))
                .andExpect(flash().attributeExists("error"));
        verify(userService, times(1)).updateUser(Mockito.anyString(), Mockito.any(AppUser.class));
    }
}
