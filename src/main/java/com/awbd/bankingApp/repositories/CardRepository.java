package com.awbd.bankingApp.repositories;

import com.awbd.bankingApp.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    Card findByAccountId(Integer accountId);

    @Query("select count(card.id) from Card card")
    Integer countCards();
}
