package com.paymybuddy.pay_my_buddy.model;

import java.math.BigInteger;
import java.security.Timestamp;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private BankAccount receiver;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private BankAccount sender;

    // private BigInteger sender_id;
    // private BigInteger receiver_id;
    private String description;
    private Double amount;

    @Column(name = "transaction_date")
    private java.sql.Timestamp transactionDate;

}
