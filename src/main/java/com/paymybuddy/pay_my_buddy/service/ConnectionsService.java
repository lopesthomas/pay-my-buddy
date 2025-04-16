package com.paymybuddy.pay_my_buddy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.paymybuddy.pay_my_buddy.DTO.ConnectionDTO;
import com.paymybuddy.pay_my_buddy.model.Connections;
import com.paymybuddy.pay_my_buddy.repository.ConnectionsRepository;
import com.paymybuddy.pay_my_buddy.repository.UserRepository;
import lombok.extern.java.Log;

@Service
@Log
public class ConnectionsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionsRepository connectionsRepository;

    public List<ConnectionDTO> getConnectionsList(String email) {
        return connectionsRepository.findByUserId_Email(email).stream()
                .map(connection -> {
                    ConnectionDTO connectionDTO = new ConnectionDTO();
                    connectionDTO.setUserEmail(email);
                    connectionDTO.setFriendEmail(connection.getFriendId().getEmail());
                    connectionDTO.setFriendName(connection.getFriendId().getUsername());
                    return connectionDTO;
                }).toList();
    }

    public ResponseEntity<?> saveConnection(ConnectionDTO connectionDTO, String authEmail) {
        try {

            if (connectionDTO.getUserEmail() == null || connectionDTO.getUserEmail().isEmpty() || !connectionDTO.getUserEmail().equals(authEmail)) {
                throw new RuntimeException("User email is null or empty");
            }
            if (connectionDTO.getFriendEmail() == null || connectionDTO.getFriendEmail().isEmpty()) {
                throw new RuntimeException("Friend email is null or empty");
            }
    
            boolean alreadyExists = connectionsRepository
            .findByUserId_EmailAndFriendId_Email(authEmail, connectionDTO.getFriendEmail())
            .isPresent();
            if (alreadyExists) {
                throw new RuntimeException("Connection already exists between " + authEmail + " and " + connectionDTO.getFriendEmail());
            }
    
            Connections connection = new Connections();
            connection.setUserId(userRepository.findByEmail(authEmail));
            connection.setFriendId(userRepository.findByEmail(connectionDTO.getFriendEmail()));
            connectionsRepository.save(connection);
            log.info("Connection saved between " + authEmail + " and " + connectionDTO.getFriendEmail());
            
            return new ResponseEntity<>(HttpStatus.CREATED); // 201 Created

        } catch (Exception e) {
            log.severe("Error saving connection: " + e.getMessage());
            // throw e;
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
       
    }
}
