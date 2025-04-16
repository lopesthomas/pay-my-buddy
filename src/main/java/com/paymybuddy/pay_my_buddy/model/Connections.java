package com.paymybuddy.pay_my_buddy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "connections", uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "friend_id"}) })
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Connections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser userId;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private AppUser friendId;
}
