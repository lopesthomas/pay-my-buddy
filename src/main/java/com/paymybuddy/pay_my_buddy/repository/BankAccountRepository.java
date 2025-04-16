package com.paymybuddy.pay_my_buddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paymybuddy.pay_my_buddy.model.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

}
