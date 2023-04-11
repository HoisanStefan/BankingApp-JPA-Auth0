package com.awbd.bankingApp.service;

import com.awbd.bankingApp.domain.Account;
import com.awbd.bankingApp.domain.Contract;

import java.util.List;
import java.util.Optional;

public interface ContractService {
    List<Contract> findAll();
    Contract findById(Integer id);
    Contract save(Contract contract);
    void deleteById(Integer id);
    Optional<Contract> findByBankIdAndClientId(Integer bankId, String clientId);
    Integer getCount();
    List<Contract> findByBankId(Integer bankId);
    List<Contract> findByClientId(String clientId);

    void deleteAccount(Account account);
}
