package com.paymybuddy.pay_my_buddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymybuddy.pay_my_buddy.model.Transaction;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
