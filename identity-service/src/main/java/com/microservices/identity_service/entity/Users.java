package com.microservices.identity_service.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.microservices.identity_service.enums.UserRoles;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phoneNumber;
    private String age;
    private String email;
    private String password;
    private String address;
    private String proof;
    private UserRoles userRoles;

    private String resetToken;

//    public UserDTO getUserDto(){
//        UserDTO userDTO = new UserDTO();
//
//        userDTO.setId(id);
//        userDTO.setName(name);
//        userDTO.setAge(age);
//        userDTO.setPhoneNumber(phoneNumber);
//        userDTO.setAddress(address);
//        userDTO.setProof(proof);
//        userDTO.setUserType(userRoles);
//
//        return userDTO;
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRoles.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", age='" + age + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", proof='" + proof + '\'' +
                ", userRoles=" + userRoles +
                ", resetToken='" + resetToken + '\'' +
                '}';
    }

}

