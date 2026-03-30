package com.microservices.AdminService.service;

import com.microservices.AdminService.dto.*;
import com.microservices.AdminService.entity.Cards;
import com.microservices.AdminService.entity.Users;
import com.microservices.AdminService.enums.CardType;
import com.microservices.AdminService.enums.UserRoles;
import com.microservices.AdminService.exception.CardNotFoundException;
import com.microservices.AdminService.exception.ResourceNotFoundException;
import com.microservices.AdminService.exception.UserNotFoundException;
import com.microservices.AdminService.feign.IdentityInterface;
import com.microservices.AdminService.mapper.Mapper;
import com.microservices.AdminService.repository.CardRepository;
import com.microservices.AdminService.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private Mapper mapper;

    //    For Admin
//    Get All cards by cardtype
//    Get All cards List by Bank Name & Card Type
//    Getting card all by filters (issuedAt , expiredBefore , name , bankName , cardType)
//    Finding User by roles
//    Find User by name
//    Find User by Name & Role
//    Creating a card

    public CardInfoDTO createCard(CreateCardDTO createCardDTO) throws IllegalAccessException {

        Users user = userRepository.findByName(createCardDTO.getUserName()).orElseThrow(() -> new UserNotFoundException("User Not Found"));

        Cards cards = mapper.mapToCreateCard(createCardDTO);
        cards.setUsers(user);

        Cards savedCard = cardRepository.save(cards);
        return mapper.mapFromCardToCardDTO(cards);
    }

    public List<CardInfoDTO> findAllCardsByCardType(String cardType){

        CardType type = CardType.valueOf(cardType.toUpperCase());

        return cardRepository
                .findAllByCardType(type)
                .stream()
                .map(mapper::mapFromCardToCardDTO)
                .collect(Collectors.toList());
    }

    public List<CardInfoDTO> findAllCardsByBankNameAndCardType(String bankName , String cardType){

        CardType type = CardType.valueOf(cardType.toUpperCase());

        return cardRepository
                .findAllCardsByBankNameAndCardType(bankName, type)
                .stream()
                .map(mapper::mapFromCardToCardDTO)
                .collect(Collectors.toList());

    }

    public List<CardInfoDTO> findAllCardByFilters(LocalDate issuedAfter , LocalDate expiredBefore , String bankName , String cardType){

        CardType type = CardType.valueOf(cardType.toUpperCase());
        return cardRepository
                .findAllCardByFilters(issuedAfter , expiredBefore , bankName , type)
                .stream()
                .map(mapper::mapFromCardToCardDTO)
                .collect(Collectors.toList());
    }

    public List<UserInfoDTO> findAllUserByRole(String userRoles){

        UserRoles role = UserRoles.valueOf(userRoles.toUpperCase());
        List<Users> users = userRepository.findAllUserByUserRole(role);

        return users
                .stream()
                .map(user -> {
                    List<CardInfoDTO> cards = cardRepository.findCardByUserId(user.getId())
                            .stream()
                            .map(mapper::mapFromCardToCardDTO)
                            .toList();
                    return mapper.mapFromUserToUserDTO(user , cards);
                }).toList();
    }

    public UserInfoDTO findUserByName(String name){

        List<Cards> cardsList = cardRepository.findCardByUsersName(name);

        Users users = userRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<CardInfoDTO> cardInfoDTO = new ArrayList<>();
        for (Cards cards : cardsList){
            cardInfoDTO.add(mapper.mapFromCardToCardDTO(cards));
        }
        return mapper.mapFromUserToUserDTO(users , cardInfoDTO);
    }

    public UserBankerInfoDTO findUserByBankerName(String bankerName){

        Users users = userRepository.findByName(bankerName).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapper.mapFromUserToBankerInfoDTO(users);
    }

    public List<?> findAllUsersByBankNameAndUserRole(String bankName , String userRoles){

        UserRoles role = UserRoles.valueOf(userRoles.toUpperCase());

        List<Users> userList = userRepository.findAllUsersByBankNameAndUserRole(bankName , role);

        List<UserInfoDTO> userInfoList = new ArrayList<>();
        List<UserBankerInfoDTO> bankerInfoList = new ArrayList<>();
        for (Users users : userList) {

            if (users.getUserRoles().equals(UserRoles.USER)) {
                List<Cards> cards = cardRepository.findAllCardByBankNameAndUser(bankName, users.getName(), role);
                List<CardInfoDTO> cardInfoList = new ArrayList<>();
                for (Cards card : cards) {
                    cardInfoList.add(mapper.mapFromCardToCardDTO(card));
                }
                 userInfoList.add(mapper.mapFromUserToUserDTO(users , cardInfoList));
            }
                bankerInfoList.add(mapper.mapFromUserToBankerInfoDTO(users));
        }
        if (role == UserRoles.USER){
            return userInfoList;
        }
        return bankerInfoList;
    }

    public Map<String , Long> getCardCountPerBank(){
        List<Cards> allCards = cardRepository.findAll();
        return allCards
                .stream()
                .collect(Collectors
                        .groupingBy(
                                Cards::getBankName,Collectors.counting()
                        ));
    }


//    For User
//    Getting Card by Bankname and UserName
//    Getting Card by User Name , Bank Name , and CardType
//    Getting Card by CardNumber
//    Getting card by filters (userName,issuedAt , expiredBefore , name , bankName , cardType)
//    Finding User by Name and Role
//    Update the card details , using "getting card by filter"

    public CardInfoDTO findCardBankNameAndUserName(String bankName , String name , String cardType , String cvv){

        CardType type = CardType.valueOf(cardType.toUpperCase());

        Cards cards = cardRepository.findCardByBankNameUserNameAndCardType(bankName , name , type , cvv).orElseThrow(() -> new CardNotFoundException("Card not Found"));

        return mapper.mapFromCardToCardDTO(cards);

    }

    public CardInfoDTO findCardByFilter(Date issuedAfter ,Date expiredBefore ,String name ,String bankName , String cardType){

        Users users = userRepository.findByName(name).orElseThrow(() -> new UserNotFoundException("User not Found"));

        CardType type = CardType.valueOf(cardType.toLowerCase());

        Cards cards = cardRepository.findSpecificCardByFilters(convertDateToLocalDate(issuedAfter) , convertDateToLocalDate(expiredBefore) , name , bankName , type).orElseThrow(() -> new CardNotFoundException("Card Not Found"));

        return mapper.mapFromCardToCardDTO(cards);
    }


    public UserInfoDTO findUserByNameAndRole(String name , String role){

        UserRoles roles = UserRoles.valueOf(role.toUpperCase());

        Users user = userRepository.findByName(name).orElseThrow(() -> new UserNotFoundException("User Not Found"));

        List<Cards> cardsList = cardRepository.findCardByUserId(user.getId());

        List<CardInfoDTO> cardInfoList = new ArrayList<>();

        for (Cards cards : cardsList){
            cardInfoList.add(mapper.mapFromCardToCardDTO(cards));
        }

        return mapper.mapFromUserToUserDTO(user , cardInfoList);
    }

    public CardInfoDTO updateCardDetails(UpdateCardDetailDTO cardDetailDTO){

        Cards cards = cardRepository.findCardByCardNumber(cardDetailDTO.getCardNumber()).orElseThrow(() -> new CardNotFoundException("Card Not Found"));

        cards.setBankName(cardDetailDTO.getBankName());
        cards.setIssueAt(convertDateToLocalDate(cardDetailDTO.getIssueAt()));
        cards.setExpireAt(convertDateToLocalDate(cardDetailDTO.getExpireAt()));
        cards.setCardType(CardType.valueOf(cardDetailDTO.getCardType().toLowerCase()));

        return mapper.mapFromCardToCardDTO(cards);
    }


    public boolean deleteCardByCardNumber(String cardNumber){

        Cards cards = cardRepository.findCardByCardNumber(cardNumber).orElseThrow(() -> new CardNotFoundException("Card Not Found"));

        cardRepository.deleteById(cards.getId());

        return true;
    }

//    For Banker
//    Getting all the Users from a Specific bank
//    Getting all the Users /Cards by specific bank and card type
//    Getting all Cards between issuedAt & expiredAt , bankName , by CardType
//    Getting all Cards by filter
//    Getting all cards by bankName and cardType

//    Need to ask chatGPT for how to export jwtUtil userLoggedIn Functionality in microservice
    public List<UserBankerInfoDTO> getAllUserBankersOfBank(String bankName){

        List<Users> usersList = userRepository.findAllUserByUserRole(UserRoles.BANKER);

        return usersList.stream().map(user -> mapper.mapFromUserToBankerInfoDTO(user)).toList();
    }

    public List<UserInfoDTO> getAllUsersOfBank(String bankName){

        List<Users> usersList = userRepository.findAllUserByUserRole(UserRoles.USER)
                .stream()
                .filter(user -> user.getCards().get(0).getBankName().equalsIgnoreCase(bankName))
                .toList();
        return usersList.stream().map(users -> {
            List<CardInfoDTO> cardInfo = cardRepository.findCardByUserId(users.getId()).stream().map(cards -> mapper.mapFromCardToCardDTO(cards)).toList();
            return mapper.mapFromUserToUserDTO(users , cardInfo);
        }).toList();
    }

    public List<UserInfoDTO> findAllUserByBankAndCardType(String bankName , String cardType){

        CardType type = CardType.valueOf(cardType.toUpperCase());

        List<Cards> cardList = cardRepository.findAllCardsByBankNameAndCardTypes(bankName,type);

        List<Users> userList = userRepository.findAllUsersByBankNameAndUserRole(bankName , UserRoles.USER);

        List<UserInfoDTO> userInfoList = new ArrayList<>();

//        List<CardInfoDTO> carInfoListForUser = new ArrayList<>();

        for (Users user : userList){

            List<CardInfoDTO> cardInfoListForUser = cardList
                    .stream()
                    .filter(card -> card.getUsers().getId().equals(user.getId()))
                    .map(mapper::mapFromCardToCardDTO)
                    .toList();
//            for (Cards card : cardList){
//                carInfoListForUser.add(mapper.mapFromCardToCardDTO(card));
//            }
//            userInfoList.add(mapper.mapFromUserToUserDTO(user,carInfoListForUser));
        }
        return userInfoList;
    }

    public List<CardInfoDTO> findAllCardsForUserForBankName(String bankName , String userName){

        List<Cards> cardList = cardRepository.findAllCardByBankNameAndUser(bankName , userName , UserRoles.USER);

        List<CardInfoDTO> cardInfoDTO = new ArrayList<>();

        for (Cards card : cardList){
            cardInfoDTO.add(mapper.mapFromCardToCardDTO(card));
        }

        return cardInfoDTO;

    }

    public CardInfoDTO findCardsByUserBankAndCardType(String name , String bankName , String cardType){

        CardType type = CardType.valueOf(cardType.toUpperCase());

        return cardRepository.findCardByUsernameBankNameAndCardType(name , bankName , type).map(mapper::mapFromCardToCardDTO).get();
    }

    public CardInfoDTO findCardByCardNumber(String cardNumber){

        Cards cards = cardRepository.findCardByCardNumber(cardNumber).orElseThrow(() -> new CardNotFoundException("Card Not Found"));

        return mapper.mapFromCardToCardDTO(cards);

    }

    public List<UserInfoDTO> findAllUsersByCardFilters(Date issueAt , Date expiredBefore , String bankName , String cardType){

        CardType type = CardType.valueOf(cardType.toUpperCase());

        List<Cards> cardList = cardRepository.findAllCardByFilters(convertDateToLocalDate(issueAt) , convertDateToLocalDate(expiredBefore) , bankName , type);

        List<Long> cardUserIdList = cardList.stream().map(cards -> cards.getUsers().getId()).distinct().toList();

        List<UserInfoDTO> usersList = new ArrayList<>();

        for (Long userId : cardUserIdList){

            Users user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found"));

            List<Cards> cardsList = cardRepository.findCardByUserId(userId).stream().filter(cards -> cards.getCardType().equals(type)).toList();

            List<CardInfoDTO> cardInfoList = new ArrayList<>();

            for (Cards card : cardList){
                cardInfoList.add(mapper.mapFromCardToCardDTO(card));
            }

            usersList.add(mapper.mapFromUserToUserDTO(user , cardInfoList));
        }
        return usersList;
    }


    public static LocalDate convertDateToLocalDate(Date date){
        if (date == null){
            return null;
        }

        return date
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    private CardType stringTypeToCardType(String cardType){

        return switch (cardType.toLowerCase()){
            case "credit" -> CardType.CREDIT;
            case "debit" -> CardType.DEBIT;
            case "visa" -> CardType.VISA;
            default -> throw new IllegalStateException("Unexpected value: " + cardType.toLowerCase());
        };
    }



    private UserRoles stringTypeToUserRole(String userRoles){

        return switch (userRoles.toLowerCase()){
            case "admin" -> UserRoles.ADMIN;
            case "banker" -> UserRoles.BANKER;
            case "user" -> UserRoles.USER;
            default -> throw new IllegalStateException("Unexpected value: " + userRoles.toLowerCase());
        };
    }

    private String userRoleToStringType(UserRoles userRoles){

        return switch (userRoles){
            case ADMIN -> "ADMIN";
            case BANKER -> "BANKER";
            case USER -> "USER";
        };
    }


//    FeignClientInterface

    @Autowired
    private IdentityInterface identityInterface;

    public UserBankerInfoDTO  saveBanker(){

        UserDTO user = identityInterface.saveBanker();

        Users userToBeSaved = mapper.mapForUserDtoToUserSaveInMicroservice(user);

        userRepository.save(userToBeSaved);

        return mapper.mapFromUserToBankerInfoDTO(userToBeSaved);
    }

    public UserInfoDTO  saveUser(){

        UserDTO user = identityInterface.saveUser();

        Users userToBeSaved = mapper.mapForUserDtoToUserSaveInMicroservice(user);

        userRepository.save(userToBeSaved);

        return mapper.mapFromUserToUserInfoDTO(userToBeSaved);
    }

    public UserDTO getUser(){

        return identityInterface.getTheLoggedInUser();
    }
}
