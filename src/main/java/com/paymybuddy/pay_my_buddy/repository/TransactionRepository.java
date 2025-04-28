package com.paymybuddy.pay_my_buddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymybuddy.pay_my_buddy.model.Transaction;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
   List<Transaction> findBySender_UserId_Id(Long userId);

}
