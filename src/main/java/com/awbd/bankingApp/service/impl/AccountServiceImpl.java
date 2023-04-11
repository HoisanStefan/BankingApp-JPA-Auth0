package com.awbd.bankingApp.service.impl;

import com.awbd.bankingApp.domain.Account;
import com.awbd.bankingApp.domain.Card;
import com.awbd.bankingApp.domain.Client;
import com.awbd.bankingApp.exceptions.ResourceNotFoundException;
import com.awbd.bankingApp.repositories.AccountRepository;
import com.awbd.bankingApp.service.AccountService;
import com.awbd.bankingApp.service.CardService;
import com.awbd.bankingApp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CardService cardService;
    private final ClientService clientService;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, CardService cardService, ClientService clientService) {
        this.accountRepository = accountRepository;
        this.cardService = cardService;
        this.clientService = clientService;
    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new LinkedList<>();
        accountRepository.findAll().iterator().forEachRemaining(accounts::add);
        return accounts;
    }

    @Override
    public Account findById(Integer id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new ResourceNotFoundException("account " + id + " not found");
        }
        return accountOptional.get();
    }

    @Override
    public Account save(Account account) {
        System.out.println(account);
        return accountRepository.save(account);
    }

    @Override
    public void deleteById(Integer id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new RuntimeException("Account not found!");
        }
        Account account = accountOptional.get();

        Card card = cardService.findByAccountId(account.getId());
        cardService.deleteById(card.getId());

        accountRepository.save(account);
        accountRepository.deleteById(id);
    }

    @Override
    public List<Account> findByBankIdAndClientId(Integer bankId, String clientId) {
        Client client = clientService.findByUid(clientId);
        return accountRepository.findByBankIdAndClientId(bankId, client.getId());
    }

    @Override
    public Integer getCount() {
        return accountRepository.countAccounts();
    }

    @Override
    public List<Account> findByBankId(Integer bankId) {
        List<Account> accounts = new LinkedList<>();
        accountRepository.findByBankId(bankId).iterator().forEachRemaining(accounts::add);
        return accounts;
    }

    @Override
    public List<Account> findByClientId(String clientId) {
        List<Account> accounts = new LinkedList<>();
        Client client = clientService.findByUid(clientId);
        accountRepository.findByClientId(client.getId()).iterator().forEachRemaining(accounts::add);
        return accounts;
    }
}
