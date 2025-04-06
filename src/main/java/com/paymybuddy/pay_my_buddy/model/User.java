package com.paymybuddy.pay_my_buddy.model;

import java.math.BigInteger;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_db")
public class User {
    @Id
    private Long id;

    private String user_name;
    private String email;
    private String password;
    
}
