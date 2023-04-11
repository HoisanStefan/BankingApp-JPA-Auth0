package com.awbd.bankingApp.service;

import com.awbd.bankingApp.domain.Bank;

import java.util.List;

public interface BankService {
    List<Bank> findAll();
    Bank findById(Integer id);
    Bank save(Bank bank);
    void deleteById(Integer id);
}
