package com.awbd.bankingApp.repositories;

import com.awbd.bankingApp.domain.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Integer> {
    List<Credit> findByBankIdAndClientId(Integer bankId, Integer clientId);
    List<Credit> findByBankId(Integer bankId);
    List<Credit> findByClientId(Integer clientId);

    @Query("select count(credit.id) from Credit credit")
    Integer countCredits();
}
