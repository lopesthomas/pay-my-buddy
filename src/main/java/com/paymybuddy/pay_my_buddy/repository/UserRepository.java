package com.paymybuddy.pay_my_buddy.repository;

import com.paymybuddy.pay_my_buddy.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
    AppUser findByEmail(String email);
    boolean existsByEmail(String email);
}
