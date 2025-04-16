package com.paymybuddy.pay_my_buddy.model;

import java.math.BigInteger;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bank_account")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount {
    @Id
    private Long idbank_account;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user_id;

    private Double balance;

}
