package com.paymybuddy.pay_my_buddy.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private Long id;
    private String senderEmail;
    private String senderUsername;
    private String receiverEmail;
    private String receiverUsername;
    private String description;
    private BigDecimal amount;
    private String transactionDate;

}
