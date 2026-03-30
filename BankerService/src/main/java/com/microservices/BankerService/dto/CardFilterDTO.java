package com.microservices.BankerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardFilterDTO {

    private Date issuedAt;
    private Date expiredBefore;
    private String bankName;
    private String cardType;
}
