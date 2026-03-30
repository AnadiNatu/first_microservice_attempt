package com.microservices.AdminService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCardDetailDTO {

    private String cardNumber;
    private String bankName;
    private Date issueAt;
    private Date expireAt;
    private String cardType;

}
