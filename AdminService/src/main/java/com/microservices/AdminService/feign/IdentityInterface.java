package com.microservices.AdminService.feign;

import com.microservices.AdminService.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "identity-service" , url = "http://localhost:8085/api/auth")
public interface IdentityInterface {

    @PostMapping("/saveBanker")
    public UserDTO saveBanker();


    @PostMapping("/saveUser")
    public UserDTO saveUser();

    @GetMapping("/getLoggedInUser")
    public UserDTO getTheLoggedInUser();

}
