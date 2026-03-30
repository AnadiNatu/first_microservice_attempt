package com.microservices.identity_service.service;

import com.microservices.identity_service.dto.BankerSignUpDTO;
import com.microservices.identity_service.dto.UserDTO;
import com.microservices.identity_service.dto.UserSignUpDTO;
import com.microservices.identity_service.entity.Users;
import com.microservices.identity_service.enums.UserRoles;
import com.microservices.identity_service.mapper.Mapper;
import com.microservices.identity_service.repository.UserRepository;
import com.microservices.identity_service.security.JwtUtil;
import com.microservices.identity_service.security.UserServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MailService mailService;

    @Autowired
    private Mapper mapper;

    @PostConstruct
    public void createAdmin(){

        Optional<Users> optionalUser = userRepository.findByUserRoles(UserRoles.ADMIN);

        if (optionalUser.isEmpty()){

            Users users = new Users();
            users.setName("ADMIN");
            users.setPhoneNumber("1234567890");
            users.setAge("100");
            users.setEmail("admin@test.com");
            users.setPassword(new BCryptPasswordEncoder().encode("admin"));
            users.setAddress(null);
            users.setProof(null);
            users.setUserRoles(UserRoles.ADMIN);

            userRepository.save(users);

            System.out.println("Admin is created successfully");
        }
        else {
            System.out.println("Admin already created");
        }
    }

    public UserDTO bankerSignup(BankerSignUpDTO dto){

        Users bankerUser = mapper.mapForUserFromBankerSignUpDTO(dto);

        return mapper.mapForUserDTOFromUser(userRepository.save(bankerUser));

    }

    public UserDTO userSignup(UserSignUpDTO dto){

        Users user = mapper.mapForUserFromUsersSignUpDTO(dto);

        return mapper.mapForUserDTOFromUser(userRepository.save(user));

    }

    public boolean hasUserWithEmail(String email){
        return userRepository.findFirstByEmail(email).isPresent();
    }

    public void sendResetToken(String email){

        Users users = userRepository.findFirstByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
//        Changes
        UserDetails userDetails = userService.loadUserByUsername(users.getEmail());
        String resetToken = jwtUtil.generateToken(userDetails);

        users.setResetToken(resetToken);
        userRepository.save(users);

        String resetLink = "http://localhost:4200/reset-password?token="+resetToken;
        mailService.sendEmail(email , "Password Reset" , "Click the link to reset the password : " +  resetLink);

    }


    public boolean validateResetToken(String token , String email){

        String extractedEmail = jwtUtil.extractUsername(token);

        if (!extractedEmail.equals(email)){
            return false;
        }

        UserDetails userDetails = userService.loadUserByUsername(email);

        return jwtUtil.isTokenValid(token,userDetails);
    }

    public String resetPassword(String email , String token , String newPassword){

        if (!validateResetToken(token, email)){
            throw new IllegalArgumentException("Invalid or expired token");
        }

        Users users = userRepository.findFirstByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        users.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(users);

        return "Password reset successfully";
    }

    public UserDTO getLoggedInUser(){

        Users users = jwtUtil.getLoggedInUser();

        return mapper.mapForUserDTOFromUser(users);
    }

    public UserDTO saveBankerForService(String email) {

        if (hasUserWithEmail(email)){
            return mapper.mapForUserDTOFromUser(userRepository.findFirstByEmail(email).get());
        }
        return null;
    }


    public UserDTO saveUserForService(String email) {

        if (hasUserWithEmail(email)){
            return mapper.mapForUserDTOFromUser(userRepository.findFirstByEmail(email).get());
        }
        return null;
    }
}
