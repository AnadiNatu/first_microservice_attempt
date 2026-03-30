package com.microservices.AdminService.controller;

import com.microservices.AdminService.dto.*;
import com.microservices.AdminService.exception.CardNotFoundException;
import com.microservices.AdminService.exception.ResourceNotFoundException;
import com.microservices.AdminService.exception.UserNotFoundException;
import com.microservices.AdminService.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin/")
@CrossOrigin("*")
public class AdminController {
//    For Admin
//    Get All cards by cardtype
//    Get All cards List by Bank Name & Card Type
//    Getting card all by filters (issuedAt , expiredBefore , name , bankName , cardType)
//    Finding User by roles
//    Find User by name
//    Find User by Name & Role
//    Creating a card

    @Autowired
    private AdminService adminService;

    @PostMapping("create")
    public ResponseEntity<CardInfoDTO> createCard(@RequestBody CreateCardDTO cardDTO){
        try {
            CardInfoDTO response = adminService.createCard(cardDTO);
            return new ResponseEntity<>(response , HttpStatus.CREATED);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(null , HttpStatus.FORBIDDEN);
        }catch (UserNotFoundException ex){
            return new ResponseEntity<>(null , HttpStatus.NOT_FOUND);
        }catch(Exception ex){
            return new ResponseEntity<>(null , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("type/{cardType}")
    public ResponseEntity<List<CardInfoDTO>> getCardsByType(@PathVariable String cardType){

        try
        {
            List<CardInfoDTO> result = adminService.findAllCardsByCardType(cardType);
            return ResponseEntity.ok(result);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("bank/{bankName}/type/{cardType}")
    public ResponseEntity<List<CardInfoDTO>> getCardByBankAndType(@PathVariable String bankName , @PathVariable String cardType){

        try{
            List<CardInfoDTO> result = adminService.findAllCardsByBankNameAndCardType(bankName, cardType);
            return ResponseEntity.ok(result);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("filter")
    public ResponseEntity<List<CardInfoDTO>> filterCard( @RequestParam("issuedAfter") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issuedAfter,
                                                         @RequestParam("expiredBefore") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiredBefore,
                                                         @RequestParam("bankName") String bankName,
                                                         @RequestParam("cardType") String cardType){

        try{
            List<CardInfoDTO> result = adminService.findAllCardByFilters(issuedAfter, expiredBefore, bankName, cardType);
            return ResponseEntity.ok(result);
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("users/role/{userRole}")
    public ResponseEntity<List<UserInfoDTO>> getUserByRole(@PathVariable String userRoles){

            try {
                List<UserInfoDTO> result = adminService.findAllUserByRole(userRoles);
                return ResponseEntity.ok(result);
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().build();
            }

    }

    @GetMapping("user/name/{name}")
    public ResponseEntity<UserInfoDTO> getUserByName(@PathVariable String name){

        try{
            UserInfoDTO result = adminService.findUserByName(name);
            return ResponseEntity.ok(result);
        }catch (ResourceNotFoundException ex){
            return new ResponseEntity<>(null , HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("banker/name/{bankerName}")
    public ResponseEntity<UserBankerInfoDTO> getBankerByName(@PathVariable String bankerName){
        try{
            UserBankerInfoDTO result = adminService.findUserByBankerName(bankerName);
            return ResponseEntity.ok(result);
        }catch (ResourceNotFoundException ex){
            return new ResponseEntity<>(null , HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("user")
    public ResponseEntity<?> getUsersByBankAndRole(@RequestParam String bankName , @RequestParam String userRoles){
        try{
            return ResponseEntity.ok(adminService.findAllUsersByBankNameAndUserRole(bankName, userRoles));

        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body("Invalid User Role Provided");
        }
    }

    @GetMapping("count/bank")
    public ResponseEntity<Map<String , Long>> getCardCountBank(){
        return ResponseEntity.ok(adminService.getCardCountPerBank());
    }
//    For User
//    Getting Card by Bankname and UserName
//    Getting Card by User Name , Bank Name , and CardType
//    Getting Card by CardNumber
//    Getting card by filters (userName,issuedAt , expiredBefore , name , bankName , cardType)
//    Finding User by Name and Role
//    Update the card details , using "getting card by filter"

    @GetMapping("by-bank-and-user")
    public ResponseEntity<?> findCardByBankAndUser(@RequestParam String bankName , @RequestParam String name , @RequestParam String cardType , @RequestParam String cvv){
        try{
            CardInfoDTO card = adminService.findCardBankNameAndUserName(bankName , name , cardType , cvv);
            return ResponseEntity.ok(card);
        }catch (CardNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body("Invalid Card Type");
        }
    }

    @GetMapping("filter-specific")
    public ResponseEntity<?> findCardByFilter(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date issuedAfter ,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date expiredBefore,
    @RequestParam String name,
    @RequestParam String bankName,
    @RequestParam String cardType){

        try {
            CardInfoDTO card = adminService.findCardByFilter(issuedAfter, expiredBefore, name, bankName, cardType);
            return ResponseEntity.ok(card);
        }catch (UserNotFoundException | CardNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body("Invalid card type");
        }
    }

    @GetMapping("user-role")
    public ResponseEntity<?> findUserByNameAndRole(@RequestParam String name , @RequestParam String role){
        try {
            UserInfoDTO userInfoDTO = adminService.findUserByNameAndRole(name, role);
            return ResponseEntity.ok(userInfoDTO);
        }catch (UserNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body("Invalid User Roles .");
        }
    }

    @PutMapping("update")
    public ResponseEntity<?> updateCardDetails(@RequestBody UpdateCardDetailDTO updateCardDetailDTO){
        try{
            CardInfoDTO updateCard = adminService.updateCardDetails(updateCardDetailDTO);
            return ResponseEntity.ok(updateCard);
        }catch (CardNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body("Invalid Card Type");
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<?> deleteCardByCardNumber(@RequestParam String cardNumber) {
        try {
            boolean deleted = adminService.deleteCardByCardNumber(cardNumber);
            return ResponseEntity.ok("Card deleted successfully: " + deleted);
        } catch (CardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//_____

    @GetMapping("bank/bankers")
    public List<UserBankerInfoDTO>  getAllUserBankersOfBank(@RequestParam String bankName){

        List<UserBankerInfoDTO> bankers = adminService.getAllUserBankersOfBank(bankName);

        return bankers;
    }

    @GetMapping("bank/users")
    public List<UserInfoDTO> getAllUsersOfBank(@RequestParam String bankName){

        List<UserInfoDTO> users = adminService.getAllUsersOfBank(bankName);

        return users;
    }

    @GetMapping("bank/users-by-cardType")
    public List<UserInfoDTO> getAllUsersOfBankers(@RequestParam String bankName , @RequestParam String cardType){

        try{
            List<UserInfoDTO> users = adminService.findAllUserByBankAndCardType(bankName, cardType);
            return (users);
        }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("No User List Found");
        }
    }

    @GetMapping("user/cards")
    public List<CardInfoDTO> findAllCardsForUserForBankName(@RequestParam String bankName , @RequestParam String userName){

        List<CardInfoDTO> card = adminService.findAllCardsForUserForBankName(bankName, userName);

        return (card);

    }

    @GetMapping("user/bank/type")
    public CardInfoDTO findCardsByUserBankAndCardType(@RequestParam String name , @RequestParam String bankName , @RequestParam String cardType){

        try{
            CardInfoDTO cards = adminService.findCardsByUserBankAndCardType(name, bankName, cardType);
            return (cards);
        }catch (CardNotFoundException ex){
            throw new CardNotFoundException("Card Not Found");
        }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("No User List Found");

        }
    }

    @GetMapping("by-card-number")
    public CardInfoDTO findCardByCardNumber(@RequestParam String cardNumber) {
        try {
            CardInfoDTO card = adminService.findCardByCardNumber(cardNumber);
            return (card);
        } catch (CardNotFoundException e) {
            throw new CardNotFoundException("Card Not Found");
        }
    }

    @GetMapping("filter/users")
    public List<UserInfoDTO> findAllUsersByCardFilters(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date issueAt,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date expiredBefore,
            @RequestParam String bankName,
            @RequestParam String cardType) {
        try {
            return (adminService.findAllUsersByCardFilters(issueAt, expiredBefore, bankName, cardType));
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("User Not Found");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid card type.");
        }
    }
}

