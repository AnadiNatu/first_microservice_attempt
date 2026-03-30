package com.microservices.identity_service.security;

import com.microservices.identity_service.entity.Users;
import com.microservices.identity_service.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {


    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    @Autowired
    private UserRepository userRepository;


    public String generateToken(UserDetails userDetails) {

        Map<String,Object> claims = new HashMap<>();
// Changes
        claims
                .put("authorities" , userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ EXPIRATION_TIME))
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);  // this line of code converts the string into a string array for .hmacShaKeyFor() method
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String extractUsername(String token) {
        return extractClaim(token , Claims::getSubject);
    }

    public <T> T extractClaim(String token , Function<Claims,T> claimsResolver){

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        return  extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token){
        return extractClaim(token , Claims::getExpiration);
    }

    public List<GrantedAuthority> extractAuthorities(String token){

        Claims claim = extractAllClaims(token);

        List<String> roles = claim.get("authorities" , List.class);

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    public Users getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()){
            Users user = (Users) authentication.getPrincipal();
            Optional<Users> optionalUser= userRepository.findById(user.getId());
            return optionalUser.orElse(null);
        }

        return null;

    }

}
