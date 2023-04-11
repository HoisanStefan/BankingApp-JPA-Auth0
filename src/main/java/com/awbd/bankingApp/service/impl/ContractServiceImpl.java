package com.awbd.bankingApp.service.impl;

import com.awbd.bankingApp.domain.Account;
import com.awbd.bankingApp.domain.Client;
import com.awbd.bankingApp.domain.Contract;
import com.awbd.bankingApp.exceptions.ResourceNotFoundException;
import com.awbd.bankingApp.repositories.ContractRepository;
import com.awbd.bankingApp.service.AccountService;
import com.awbd.bankingApp.service.ClientService;
import com.awbd.bankingApp.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final AccountService accountService;
    private final ClientService clientService;

    @Autowired
    public ContractServiceImpl(ContractRepository contractRepository, AccountService accountService, ClientService clientService) {
        this.contractRepository = contractRepository;
        this.accountService = accountService;
        this.clientService = clientService;
    }

    @Override
    public List<Contract> findAll() {
        List<Contract> contracts = new LinkedList<>();
        contractRepository.findAll().iterator().forEachRemaining(contracts::add);
        return contracts;
    }

    @Override
    public Contract findById(Integer id) {
        Optional<Contract> contractOptional = contractRepository.findById(id);
        if (contractOptional.isEmpty()) {
            throw new ResourceNotFoundException("contract " + id + " not found");
        }
        return contractOptional.get();
    }

    @Override
    public Contract save(Contract contract) {
        System.out.println(contract);
        return contractRepository.save(contract);
    }

    @Override
    public void deleteById(Integer id) {
        Optional<Contract> contractOptional = contractRepository.findById(id);
        if (contractOptional.isEmpty()) {
            throw new RuntimeException("Contract not found!");
        }
        Contract contract = contractOptional.get();

        List<Account> accounts = accountService.findByBankIdAndClientId(contract.getBank().getId(), contract.getClient().getUid());

        for (Account account : accounts) {
            accountService.deleteById(account.getId());
        }

        contractRepository.save(contract);
        contractRepository.deleteById(id);
    }

    @Override
    public Optional<Contract> findByBankIdAndClientId(Integer bankId, String clientId) {
        Client client = clientService.findByUid(clientId);
        return contractRepository.findByBankIdAndClientId(bankId, client.getId());
    }

    @Override
    public Integer getCount() {
        return contractRepository.countContracts();
    }

    @Override
    public List<Contract> findByBankId(Integer bankId) {
        List<Contract> contracts = new LinkedList<>();
        contractRepository.findByBankId(bankId).iterator().forEachRemaining(contracts::add);
        return contracts;
    }

    @Override
    public List<Contract> findByClientId(String clientId) {
        List<Contract> contracts = new LinkedList<>();
        Client client = clientService.findByUid(clientId);
        contractRepository.findByClientId(client.getId()).iterator().forEachRemaining(contracts::add);
        return contracts;
    }

    public void deleteAccount(Account account) {
        List<Account> accounts = accountService.findByBankIdAndClientId(account.getBank().getId(), account.getClient().getUid());
        if (accounts.size() == 1) {
            Optional<Contract> contract = findByBankIdAndClientId(account.getBank().getId(), account.getClient().getUid());
            contract.ifPresent(value -> deleteById(value.getId()));
        } else if (accounts.size() == 2) {
            accountService.deleteById(account.getId());
        }
    }
}
