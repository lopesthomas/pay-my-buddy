package com.paymybuddy.pay_my_buddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private Long id;
    private String senderEmail;
    private String receiverEmail;
    private String description;
    private Double amount;
    private String transactionDate;

}
