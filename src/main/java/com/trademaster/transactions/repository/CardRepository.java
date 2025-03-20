package com.trademaster.transactions.repository;

import com.trademaster.transactions.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {
    @Modifying
    @Query("UPDATE Card c SET c.balance = c.balance - :value WHERE c.id = :id AND c.balance >= :value")
    int debitCardBalance(@Param("id") Long id, @Param("value") BigDecimal value);
}
