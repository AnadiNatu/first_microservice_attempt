package com.microservices.BankerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankNameAndTypeDTO {

    private String bankName;
    private String cardType;

}
