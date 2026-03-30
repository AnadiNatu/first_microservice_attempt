package com.microservices.BankerService.repository;

import com.microservices.BankerService.entity.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Cards , Long> {
}
