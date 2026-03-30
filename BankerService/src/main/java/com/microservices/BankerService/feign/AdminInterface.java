package com.microservices.BankerService.feign;

import com.microservices.BankerService.dto.CardInfoDTO;
import com.microservices.BankerService.dto.UserBankerInfoDTO;
import com.microservices.BankerService.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@FeignClient(name = "admin-service" , url = "http://localhost:8081/api/admin/")
public interface AdminInterface {


    @GetMapping("bank/bankers")
    public  List<UserBankerInfoDTO> getAllUserBankersOfBank(@RequestParam String bankName);

    @GetMapping("bank/users")
    public List<UserInfoDTO> getAllUsersOfBank(@RequestParam String bankName);

    @GetMapping("bank/users-by-cardType")
    public List<UserInfoDTO> getAllUsersOfBankers(@RequestParam String bankName , @RequestParam String cardType);

    @GetMapping("user/cards")
    public List<CardInfoDTO> findAllCardsForUserForBankName(@RequestParam String bankName , @RequestParam String userName);

    @GetMapping("user/bank/type")
    public CardInfoDTO findCardsByUserBankAndCardType(@RequestParam String name , @RequestParam String bankName , @RequestParam String cardType);

    @GetMapping("/by-card-number")
    public CardInfoDTO findCardByCardNumber(@RequestParam String cardNumber);

    @GetMapping("/filter/users")
    public List<UserInfoDTO> findAllUsersByCardFilters(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date issueAt,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date expiredBefore,
            @RequestParam String bankName,
            @RequestParam String cardType);
}
