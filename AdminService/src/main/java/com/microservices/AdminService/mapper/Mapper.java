package com.microservices.AdminService.mapper;

import com.microservices.AdminService.dto.*;
import com.microservices.AdminService.entity.Cards;
import com.microservices.AdminService.entity.Users;
import com.microservices.AdminService.enums.CardType;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


//    Loop laga kar cardInfo ki list banuga mapper ki help se aur phir iss maaper method ko
@Component
public class Mapper {

    public CardInfoDTO mapFromCardToCardDTO(Cards cards) {

        CardInfoDTO cardInfoDTO = new CardInfoDTO();
        cardInfoDTO.setCardNumber(cards.getCardNumber());
        cardInfoDTO.setBankNumber(cards.getBankName());
        cardInfoDTO.setCardType(String.valueOf(cards.getCardType()));
        cardInfoDTO.setIssueAt(cards.getIssueAt());
        cardInfoDTO.setExpireAt(cards.getExpireAt());

        return cardInfoDTO;
    }


    public UserInfoDTO mapFromUserToUserDTO(Users users, List<CardInfoDTO> userCardList) {

        UserInfoDTO userInfoDTO = new UserInfoDTO();

        userInfoDTO.setName(users.getName());
        userInfoDTO.setPhoneNumber(users.getPhoneNumber());
        userInfoDTO.setAge(users.getAge());
        userInfoDTO.setEmail(users.getEmail());
        userInfoDTO.setAddress(users.getAddress());
        userInfoDTO.setProof(users.getProof());
        userInfoDTO.setUserRoles(String.valueOf(users.getUserRoles()));
        userInfoDTO.setUserCardList(userCardList);

        return userInfoDTO;

    }

    public Cards mapToCreateCard(CreateCardDTO cardDTO) throws IllegalAccessException {

        Cards cards = new Cards();

        String bankName = cardDTO.getBankName();
        cards.setBankName(bankName);

        String cardPrefix = mapBankNameToCardNumberPrefix(bankName);
        cards.setCardNumber(cardPrefix + generateRandomDigits(12));

        cards.setIssueAt((cardDTO.getIssueAt()));
        cards.setExpireAt((calculateExpiryDate()));

        int cvvStart = mapBankNameToCvvRangeStart(bankName);
        int cvvEnd = mapBankNameToCvvRangeEnd(bankName);
        cards.setCvv(String.valueOf(generateRandomNumber(cvvStart, cvvEnd)));

        if (cardDTO.getCardType().equalsIgnoreCase("Credit")) {
            cards.setCardType(CardType.CREDIT);
        } else if (cardDTO.getCardType().equalsIgnoreCase("Debit")) {
            cards.setCardType(CardType.DEBIT);
        } else {
            cards.setCardType(CardType.VISA);
        }

        return cards;
    }

    private String mapBankNameToCardNumberPrefix(String bankName) throws IllegalArgumentException {
        return switch (bankName.toLowerCase()) {
            case "hdfc" -> "1234";
            case "citi" -> "5678";
            case "icici" -> "6789";
            default -> throw new IllegalArgumentException("Invalid Bank Nme");
        };
    }


    private int generateRandomNumber(int cvvStart, int cvvEnd) {
        Random random = new Random();

        return random.nextInt(cvvEnd - cvvStart + 1) + cvvStart;
    }

    private String generateRandomDigits(int length) {
        Random random = new Random();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private int mapBankNameToCvvRangeStart(String bankName) throws IllegalAccessException {
        return switch (bankName.toLowerCase()) {
            case "hdfc" -> 200;
            case "citi" -> 500;
            case "icici" -> 600;
            default -> throw new IllegalAccessException("Invalid Bank Name");
        };
    }

    public int mapBankNameToCvvRangeEnd(String bankName) throws IllegalAccessException {
        return switch (bankName.toLowerCase()) {
            case "hdfc" -> 500;
            case "citi" -> 700;
            case "icici" -> 800;
            default -> throw new IllegalAccessException("Invalid Bank Name");
        };
    }

    public LocalDate calculateExpiryDate() {

        Date curDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        calendar.add(Calendar.YEAR, 2);

        return calendar
                .getTime()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }


    public UserInfoDTO mapFromUserToUserInfoDTO(Users users){

        UserInfoDTO bankerInfoDTO = new UserInfoDTO();

        bankerInfoDTO.setName(users.getName());
        bankerInfoDTO.setPhoneNumber(users.getPhoneNumber());
        bankerInfoDTO.setAge(users.getAge());
        bankerInfoDTO.setEmail(users.getEmail());
        bankerInfoDTO.setAddress(users.getAddress());
        bankerInfoDTO.setProof(users.getProof());

        return bankerInfoDTO;
    }

    public UserBankerInfoDTO mapFromUserToBankerInfoDTO(Users users){

        UserBankerInfoDTO bankerInfoDTO = new UserBankerInfoDTO();

        bankerInfoDTO.setName(users.getName());
        bankerInfoDTO.setPhoneNumber(users.getPhoneNumber());
        bankerInfoDTO.setAge(users.getAge());
        bankerInfoDTO.setEmail(users.getEmail());
        bankerInfoDTO.setAddress(users.getAddress());
        bankerInfoDTO.setProof(users.getProof());

        return bankerInfoDTO;
    }

    public Users mapForUserDtoToUserSaveInMicroservice(UserDTO bankerInfoDTO){

        Users users = new Users();

        users.setName(bankerInfoDTO.getName());
        users.setPhoneNumber(bankerInfoDTO.getPhoneNumber());
        users.setAge(bankerInfoDTO.getAge());
        users.setEmail(bankerInfoDTO.getEmail());
        users.setAddress(bankerInfoDTO.getAddress());
        users.setProof(bankerInfoDTO.getProof());
        users.setUserRoles(bankerInfoDTO.getUserRoles());

        return users;
    }







    //    }
//        LocalDate originalDate = inputDate
//
//        if (inputDate == null)return null;
//    public static LocalDate covertDateToLocalDate_MM_yy(Date inputDate){
//    String display = localDate.format(displayFormatter);
//    DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MM/yy");
//    To Display as "MM/yy"
//    }
//        return users;
//
//        users.setUserRoles(userDTO.getUserRoles());
//        users.setProof(userDTO.getProof());
//        users.setAddress(userDTO.getAddress());
//        users.setPassword(userDTO.getPassword());
//        users.setEmail(userDTO.getEmail());
//        users.setAge(userDTO.getAge());
//        users.setPhoneNumber(userDTO.getPhoneNumber());
//        users.setName(userDTO.getName());
//
//        Users users = new Users();
//
//    public Users mapForAdminServiceToSaveUser(UserDTO userDTO){
//
//    }
//        return users;
//
//        users.setUserRoles(userDTO.getUserRoles());
//        users.setProof(userDTO.getProof());
//        users.setAddress(userDTO.getAddress());
//        users.setPassword(userDTO.getPassword());
//        users.setEmail(userDTO.getEmail());
//        users.setAge(userDTO.getAge());
//        users.setPhoneNumber(userDTO.getPhoneNumber());
//        users.setName(userDTO.getName());
//
//        Users users = new Users();
//
//    public Users mapForAdminServiceToSaveBankerUser(Users userDTO){
//    public AllCardInfoDTO mapFromCardToAllCardInfo(Cards cards) {
//
//        AllCardInfoDTO cardInfoDTO = new AllCardInfoDTO();
//
//        cardInfoDTO.setId(cards.getId());
//        cardInfoDTO.setCardNumber(cards.getCardNumber());
//        cardInfoDTO.setBankName(cards.getBankName());
//        cardInfoDTO.setCvv(cards.getCvv());
//        cardInfoDTO.setIssueAt(cards.getIssueAt());
//        cardInfoDTO.setExpireAt(cards.getExpireAt());
//        cardInfoDTO.setCardType(cards.getCardType());
//
//        return cardInfoDTO;
//
//    }
//
//
//
//    public Cards mapFromAllCardInfoToCard(AllCardInfoDTO cards) {
//
//        Cards cardInfoDTO = new Cards();
//
//        cardInfoDTO.setId(cards.getId());
//        cardInfoDTO.setCardNumber(cards.getCardNumber());
//        cardInfoDTO.setBankName(cards.getBankName());
//        cardInfoDTO.setCvv(cards.getCvv());
//        cardInfoDTO.setIssueAt(cards.getIssueAt());
//        cardInfoDTO.setExpireAt(cards.getExpireAt());
//        cardInfoDTO.setCardType(cards.getCardType());
//
//        return cardInfoDTO;
//
//    }

}

