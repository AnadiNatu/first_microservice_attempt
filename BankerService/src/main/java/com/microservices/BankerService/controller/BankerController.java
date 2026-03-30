package com.microservices.BankerService.controller;

import com.microservices.BankerService.dto.BankNameAndTypeDTO;
import com.microservices.BankerService.dto.CardFilterDTO;
import com.microservices.BankerService.service.BankerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banker/")
@CrossOrigin("*")
public class BankerController {

    @Autowired
    private BankerService bankerService;

    @GetMapping("{getUsersOfBank}")
    public ResponseEntity<?> getAllBankersOfBank(@PathVariable(name = "getUsersOfBank") String bankName) {
        return bankerService.getAllBankers_Users_OfBank(bankName);
    }

    @GetMapping("{getAllUsersOfBank}")
    public ResponseEntity<?> getAllUsersOfBank(@PathVariable(name = "getAllUsersOfBank") String bankName) {
        return bankerService.getAllUsersOfBankBy_BankName(bankName);
    }


    @GetMapping("getUserByBankNameAndType")
    public ResponseEntity<?> getUsersByBankAndCardType(@RequestBody BankNameAndTypeDTO dto) {
        return bankerService.getAllUsersBy_BankName_And_CardType(dto.getBankName(), dto.getCardType());
    }

    @GetMapping("{cardNumber}")
    public ResponseEntity<?> getCardByCardNumber(@PathVariable(name = "cardNumber") String cardNumber){
        return bankerService.findUserCardBy_CardNumber(cardNumber);
    }

    @GetMapping("card-filter")
    public ResponseEntity<?> getUsersBy_CardFilter(@RequestBody CardFilterDTO dto){
        return bankerService.findAllUsersBy_CardFilters(dto.getIssuedAt(), dto.getExpiredBefore(), dto.getBankName(), dto.getCardType());
    }
}
