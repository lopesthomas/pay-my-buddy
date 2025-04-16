package com.paymybuddy.pay_my_buddy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.pay_my_buddy.model.Connections;

@Repository
public interface ConnectionsRepository extends JpaRepository<Connections, Long> {
    List<Connections> findByUserId_Email(String email);
    Optional<Connections> findByUserId_EmailAndFriendId_Email(String userEmail, String friendEmail);

}
