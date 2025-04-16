package com.paymybuddy.pay_my_buddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.pay_my_buddy.DTO.ConnectionDTO;
import com.paymybuddy.pay_my_buddy.service.ConnectionsService;



@RestController
@RequestMapping("/api/connections")
public class ConnectionsController {

    @Autowired
    private ConnectionsService connectionsService;

    @GetMapping
    public List<ConnectionDTO> getConnectionsList(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        String email = userDetails.getUsername();
        return connectionsService.getConnectionsList(email);
    }

    @PostMapping
    public ResponseEntity<?> postMethodName(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails, @RequestBody ConnectionDTO connectionDTO) {
        String email = userDetails.getUsername();
        return connectionsService.saveConnection(connectionDTO, email);
    }
    
}
