package com.paymybuddy.pay_my_buddy.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.paymybuddy.pay_my_buddy.DTO.ConnectionDTO;
import com.paymybuddy.pay_my_buddy.model.AppUser;
import com.paymybuddy.pay_my_buddy.model.Connections;
import com.paymybuddy.pay_my_buddy.repository.ConnectionsRepository;
import com.paymybuddy.pay_my_buddy.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

public class ConnectionsServiceTest {

    @InjectMocks
    private ConnectionsService connectionsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConnectionsRepository connectionsRepository;

    private AppUser user1;
    private AppUser user2;
    private ConnectionDTO connectionDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new AppUser();
        user1.setEmail("user1@example.com");
        user1.setUsername("User One");

        user2 = new AppUser();
        user2.setEmail("user2@example.com");
        user2.setUsername("User Two");

        connectionDTO = new ConnectionDTO();
        connectionDTO.setFriendEmail("user2@example.com");
        connectionDTO.setUserEmail("user1@example.com");
        connectionDTO.setFriendName("User Two");
    }

    @Test
    void testGetConnectionsList_validEmail_returnsConnections() {
        Connections connection = new Connections();
        connection.setUserId(user1);
        connection.setFriendId(user2);

        when(connectionsRepository.findByUserId_Email(user1.getEmail())).thenReturn(List.of(connection));

        var connectionsList = connectionsService.getConnectionsList(user1.getEmail());

        assertNotNull(connectionsList);
        assertEquals(1, connectionsList.size());
        assertEquals("user2@example.com", connectionsList.get(0).getFriendEmail());
        assertEquals("User Two", connectionsList.get(0).getFriendName());
    }

    @Test
    void testSaveConnection_validConnection_savesConnection() {
        when(userRepository.existsByEmail("user2@example.com")).thenReturn(true);
        when(userRepository.findByEmail("user1@example.com")).thenReturn(user1);
        when(userRepository.findByEmail("user2@example.com")).thenReturn(user2);
        when(connectionsRepository.findByUserId_EmailAndFriendId_Email("user1@example.com", "user2@example.com"))
                .thenReturn(Optional.empty());

        connectionsService.saveConnection(connectionDTO, "user1@example.com");

        verify(connectionsRepository, times(1)).save(any(Connections.class));
    }

    @Test
    void testSaveConnection_authEmailNull_throwsException() {
        connectionDTO.setFriendEmail("user2@example.com");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            connectionsService.saveConnection(connectionDTO, null);
        });

        assertEquals("Auth email is null or empty", thrown.getMessage());
    }

    @Test
    void testSaveConnection_friendEmailNull_throwsException() {
        connectionDTO.setFriendEmail(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            connectionsService.saveConnection(connectionDTO, "user1@example.com");
        });

        assertEquals("Friend email is null or empty", thrown.getMessage());
    }

    @Test
    void testSaveConnection_friendEmailDoesNotExist_throwsException() {
        when(userRepository.existsByEmail("user2@example.com")).thenReturn(false);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            connectionsService.saveConnection(connectionDTO, "user1@example.com");
        });

        assertEquals("Friend email does not exist in the database", thrown.getMessage());
    }

    @Test
    void testSaveConnection_alreadyExists_throwsException() {
        when(userRepository.existsByEmail("user2@example.com")).thenReturn(true);
        when(connectionsRepository.findByUserId_EmailAndFriendId_Email("user1@example.com", "user2@example.com"))
                .thenReturn(Optional.of(new Connections()));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            connectionsService.saveConnection(connectionDTO, "user1@example.com");
        });

        assertEquals("Connection already exists between user1@example.com and user2@example.com", thrown.getMessage());
    }

    @Test
    void testSaveConnection_authEmailSameFriendEmail_throwsException() {
        connectionDTO.setFriendEmail("user1@example.com");
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(true);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            connectionsService.saveConnection(connectionDTO, "user1@example.com");
        });

        assertEquals("Auth email and friend email are the same", thrown.getMessage());
    }
}
