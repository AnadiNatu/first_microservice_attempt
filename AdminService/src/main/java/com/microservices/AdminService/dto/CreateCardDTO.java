package com.microservices.AdminService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class CreateCardDTO {
    private String userName;
    private String cardNumber;
    private String bankName;
    private String cvv;
    private LocalDate issueAt;
    private LocalDate expireAt;
    private String cardType;

}
