package com.paymybuddy.pay_my_buddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionDTO {
    private String userEmail;
    private String friendEmail;
    private String friendName;

}
