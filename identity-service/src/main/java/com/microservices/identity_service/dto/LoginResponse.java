package com.microservices.identity_service.dto;

import com.microservices.identity_service.enums.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String jwt;
    private Long userId;
    private UserRoles userRoles;
}
