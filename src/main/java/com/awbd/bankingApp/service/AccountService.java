package com.awbd.bankingApp.service;

import com.awbd.bankingApp.domain.Account;

import java.util.List;

public interface AccountService {
    List<Account> findAll();
    Account findById(Integer id);
    Account save(Account account);
    void deleteById(Integer id);
    List<Account> findByBankIdAndClientId(Integer bankId, String clientId);
    Integer getCount();
    List<Account> findByBankId(Integer bankId);
    List<Account> findByClientId(String clientId);
}
