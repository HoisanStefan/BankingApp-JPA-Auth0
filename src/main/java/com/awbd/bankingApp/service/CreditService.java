package com.awbd.bankingApp.service;

import com.awbd.bankingApp.domain.Credit;

import java.util.List;

public interface CreditService {
    List<Credit> findAll();
    Credit findById(Integer id);
    Credit save(Credit credit);
    void deleteById(Integer id);
    List<Credit> findByBankIdAndClientId(Integer bankId, String clientId);
    Integer getCount();
    List<Credit> findByBankId(Integer bankId);
    List<Credit> findByClientId(String clientId);
}
