package com.awbd.bankingApp.repositories;

import com.awbd.bankingApp.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findByBankIdAndClientId(Integer bankId, Integer clientId);
    List<Account> findByBankId(Integer bankId);
    List<Account> findByClientId(Integer clientId);

    @Query("select count(account.id) from Account account")
    Integer countAccounts();
}
