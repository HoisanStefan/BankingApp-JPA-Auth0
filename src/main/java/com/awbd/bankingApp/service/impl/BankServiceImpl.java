package com.awbd.bankingApp.service.impl;

import com.awbd.bankingApp.domain.Bank;
import com.awbd.bankingApp.domain.Contract;
import com.awbd.bankingApp.domain.Credit;
import com.awbd.bankingApp.exceptions.ResourceNotFoundException;
import com.awbd.bankingApp.repositories.BankRepository;
import com.awbd.bankingApp.service.BankService;
import com.awbd.bankingApp.service.ContractService;
import com.awbd.bankingApp.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class BankServiceImpl implements BankService {
    private final BankRepository bankRepository;
    private final ContractService contractService;
    private final CreditService creditService;

    @Autowired
    public BankServiceImpl(BankRepository bankRepository, ContractService contractService, CreditService creditService) {
        this.bankRepository = bankRepository;
        this.contractService = contractService;
        this.creditService = creditService;
    }

    @Override
    public List<Bank> findAll() {
        List<Bank> banks = new LinkedList<>();
        bankRepository.findAll().iterator().forEachRemaining(banks::add);
        return banks;
    }

    @Override
    public Bank findById(Integer id) {
        Optional<Bank> bankOptional = bankRepository.findById(id);
        if (bankOptional.isEmpty()) {
            throw new ResourceNotFoundException("bank " + id + " not found");
        }
        return bankOptional.get();
    }

    @Override
    public Bank save(Bank bank) {
        System.out.println(bank);
        return bankRepository.save(bank);
    }

    @Override
    public void deleteById(Integer id) {
        Optional<Bank> bankOptional = bankRepository.findById(id);
        if (bankOptional.isEmpty()) {
            throw new RuntimeException("Bank not found!");
        }
        Bank bank = bankOptional.get();

        List<Contract> contracts = contractService.findByBankId(id);
        for (Contract contract : contracts) {
            contractService.deleteById(contract.getId());
        }

        List<Credit> credits = creditService.findByBankId(id);
        for (Credit credit : credits) {
            creditService.deleteById(credit.getId());
        }

        bankRepository.save(bank);
        bankRepository.deleteById(id);
    }
}
