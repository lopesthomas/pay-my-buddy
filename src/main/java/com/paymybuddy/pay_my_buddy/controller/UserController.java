package com.paymybuddy.pay_my_buddy.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController("/api/user")
public class UserController {

    @GetMapping("/login")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @GetMapping("/register")
    public String getMethodName2(@RequestParam String param) {
        return new String();
    }
    
    

}
