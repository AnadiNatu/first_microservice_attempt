package com.microservices.api_gateway.filter;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;

@Component
public class JwtAuthFilter implements GatewayFilter , Ordered {

    @Value("${jwt.secret}")
    private String secret;

    private Key getSignKey(){

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(Arrays.copyOf(keyBytes,32));

    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");


        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return this.onError(exchange , "Authorization header missing" , HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try{
            Claims claims =Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


            exchange = exchange
                    .mutate()
                    .request(builder -> builder.header("X-User" , claims.getSubject()))
                    .build();
        }catch (JwtException ex){
            return this.onError(exchange , "Invalid JWT Token" , HttpStatus.UNAUTHORIZED);
        }
        return chain.filter(exchange);

    }

    private Mono<Void> onError(ServerWebExchange exchange , String err , HttpStatus status){
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder(){
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
