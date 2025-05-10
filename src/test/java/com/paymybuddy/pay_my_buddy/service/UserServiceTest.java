package com.paymybuddy.pay_my_buddy.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.paymybuddy.pay_my_buddy.model.AppUser;
import com.paymybuddy.pay_my_buddy.repository.BankAccountRepository;
import com.paymybuddy.pay_my_buddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AppUser user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password123");
    }

    @Test
    void testSaveUser_validUser_savesUser() {
        
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void testSaveUserWithNullUsername() {
        
        user.setUsername(null);
        
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.saveUser(user);
        });
    
        assertEquals("Username is not valid, null or empty", thrown.getMessage());
    
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void testSaveUserWithNullPassword() {
        user.setPassword(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.saveUser(user);
        });

        assertEquals("Password is null or empty", thrown.getMessage());

        verify(userRepository, times(0)).save(user);
    }

    @Test
    void testSaveUser_invalidEmail_throwsException() {
        
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.saveUser(user);
        });
        assertEquals("Email is null or empty or already taken", thrown.getMessage());
    }

    @Test
    void testUpdateUser_validUpdate_updatesUser() {
        
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        AppUser updatedUser = new AppUser();
        updatedUser.setId(user.getId());
        updatedUser.setUsername("newUsername");
        updatedUser.setEmail("newuser@example.com");
        updatedUser.setPassword("newPassword");

        userService.updateUser(user.getEmail(), updatedUser);

        verify(userRepository, times(1)).save(user);
        assertEquals("newUsername", user.getUsername());
        assertEquals("newuser@example.com", user.getEmail());
        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    void testUpdateUser_invalidEmail_throwsException() {
        
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));

        AppUser updatedUser = new AppUser();
        updatedUser.setId(user.getId());
        updatedUser.setEmail("existingEmail@example.com");

        when(userRepository.findByEmail(updatedUser.getEmail())).thenReturn(new AppUser());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(user.getEmail(), updatedUser);
        });
        assertEquals("Email not valid or already taken", thrown.getMessage());
    }

    @Test
    void testUpdateUserInvalidAuthEmail() {
        String authEmail = null;
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(authEmail, user);
        });
        assertEquals("Auth email is null or empty", thrown.getMessage());
    }

    @Test
    void testUpdateUserWithNullId() {
        String authEmail = "test@test.com";
        AppUser userWithNullId = new AppUser();
        userWithNullId.setId(0L);
        userWithNullId.setUsername("testuser");
        userWithNullId.setEmail("");
        userWithNullId.setPassword("password123");
        
        when(userRepository.findByEmail(authEmail)).thenReturn(userWithNullId);
        when(userRepository.findById(userWithNullId.getId())).thenReturn(java.util.Optional.of(userWithNullId));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(authEmail, user);
        });
        assertEquals("User ID is null", thrown.getMessage());
    }

    @Test
    void testUpdateUserWithNewPassword() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("test");
        user.setEmail("test@test.fr");
        user.setPassword("newPassword");

        AppUser existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setUsername("test");
        existingUser.setEmail("test@test.fr");
        existingUser.setPassword("oldPasswordEncoded");

        when(userRepository.findByEmail("test@test.fr")).thenReturn(existingUser);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userService.updateUser("test@test.fr", user);

        verify(userRepository, times(1)).save(existingUser);
        verify(passwordEncoder, times(1)).encode("newPassword");
        assertEquals("encodedNewPassword", existingUser.getPassword());
    }


    @Test
    void testUpdateUserWithNullPassword() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("test");
        user.setEmail("test@test.fr");
        user.setPassword(null);
    
        AppUser existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setUsername("test");
        existingUser.setEmail("test@test.fr");
        existingUser.setPassword("encodedPassword");
    
        when(userRepository.findByEmail("test@test.fr")).thenReturn(existingUser);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(existingUser));
    
        userService.updateUser("test@test.fr", user);
    
        verify(userRepository, times(1)).save(existingUser);
        
        assertEquals("encodedPassword", existingUser.getPassword());
    }
}
