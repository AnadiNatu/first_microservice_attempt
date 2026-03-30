package com.microservices.identity_service.mapper;

import com.microservices.identity_service.dto.BankerSignUpDTO;
import com.microservices.identity_service.dto.UserDTO;
import com.microservices.identity_service.dto.UserSignUpDTO;
import com.microservices.identity_service.entity.Users;
import com.microservices.identity_service.enums.UserRoles;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Mapper {


    public Users mapForUserFromBankerSignUpDTO(BankerSignUpDTO dto){

        Users users = new Users();

        users.setName(dto.getName());
        users.setPhoneNumber(dto.getPhoneNumber());
        users.setAge(dto.getAge());
        users.setEmail(dto.getEmail());
        users.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        users.setAddress(dto.getAddress());
        users.setProof(dto.getProof());
        users.setUserRoles(UserRoles.BANKER);

        return users;
    }

    public Users mapForUserFromUsersSignUpDTO(UserSignUpDTO dto){

        Users users = new Users();

        users.setName(dto.getName());
        users.setPhoneNumber(dto.getPhoneNumber());
        users.setAge(dto.getAge());
        users.setEmail(dto.getEmail());
        users.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        users.setAddress(dto.getAddress());
        users.setProof(dto.getProof());
        users.setUserRoles(UserRoles.USER);

        return users;
    }

    public UserDTO mapForUserDTOFromUser(Users dto){

        UserDTO users = new UserDTO();

        users.setName(dto.getName());
        users.setPhoneNumber(dto.getPhoneNumber());
        users.setAge(dto.getAge());
        users.setEmail(dto.getEmail());
        users.setPassword(dto.getPassword());
        users.setAddress(dto.getAddress());
        users.setProof(dto.getProof());
        users.setUserRoles(UserRoles.USER);

        return users;

    }
}
