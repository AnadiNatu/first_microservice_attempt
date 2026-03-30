package com.microservices.BankerService.service;

import com.microservices.BankerService.dto.CardInfoDTO;
import com.microservices.BankerService.dto.UserBankerInfoDTO;
import com.microservices.BankerService.dto.UserInfoDTO;
import com.microservices.BankerService.feign.AdminInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BankerService {

    @Autowired
    private AdminInterface adminInterface;

    public ResponseEntity<?> getAllBankers_Users_OfBank(String bankName){

        List<UserBankerInfoDTO> userList = adminInterface.getAllUserBankersOfBank(bankName);

        return ResponseEntity.ok(userList);

    }

    public ResponseEntity<?> getAllUsersOfBankBy_BankName(String bankName){

        List<UserInfoDTO> userList = adminInterface.getAllUsersOfBank(bankName);

        return ResponseEntity.ok(userList);
    }

    public ResponseEntity<?> getAllUsersBy_BankName_And_CardType(String bankName , String cardType){

        List<UserInfoDTO> userList = adminInterface.getAllUsersOfBankers(bankName, cardType);

        return ResponseEntity.ok(userList);
    }

    public ResponseEntity<?> findAllCardsBy_UserNameAnd_BankName(String bankName , String userName){

        List<CardInfoDTO> card = adminInterface.findAllCardsForUserForBankName(bankName, userName);

        return ResponseEntity.ok(card);

    }

    public ResponseEntity<?> findAllCardBy_UserName_BankName_And_CardType(String userName , String bankName , String cardType){

        CardInfoDTO card = adminInterface.findCardsByUserBankAndCardType(userName, bankName, cardType);

        return ResponseEntity.ok(card);
    }

    public ResponseEntity<?> findUserCardBy_CardNumber(String cardNumber){

        CardInfoDTO card = adminInterface.findCardByCardNumber(cardNumber);

        return ResponseEntity.ok(card);

    }

    public ResponseEntity<?> findAllUsersBy_CardFilters(Date issuedAt , Date expiredBefore , String bankName , String cardType){

        List<UserInfoDTO> userDTO = adminInterface.findAllUsersByCardFilters(issuedAt, expiredBefore, bankName, cardType);

        return ResponseEntity.ok(userDTO);

    }
}
