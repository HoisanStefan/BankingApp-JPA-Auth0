package com.awbd.bankingApp.service.impl;

import com.awbd.bankingApp.domain.Client;
import com.awbd.bankingApp.domain.Credit;
import com.awbd.bankingApp.exceptions.ResourceNotFoundException;
import com.awbd.bankingApp.repositories.CreditRepository;
import com.awbd.bankingApp.service.ClientService;
import com.awbd.bankingApp.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class CreditServiceImpl implements CreditService {
    private final CreditRepository creditRepository;

    private final ClientService clientService;

    @Autowired
    public CreditServiceImpl(CreditRepository creditRepository, ClientService clientService) {
        this.creditRepository = creditRepository;
        this.clientService = clientService;
    }

    @Override
    public List<Credit> findAll() {
        List<Credit> credits = new LinkedList<>();
        creditRepository.findAll().iterator().forEachRemaining(credits::add);
        return credits;
    }

    @Override
    public Credit findById(Integer id) {
        Optional<Credit> creditOptional = creditRepository.findById(id);
        if (creditOptional.isEmpty()) {
            throw new ResourceNotFoundException("credit " + id + " not found");
        }
        return creditOptional.get();
    }

    @Override
    public Credit save(Credit credit) {
        System.out.println(credit);
        return creditRepository.save(credit);
    }

    @Override
    public void deleteById(Integer id) {
        Optional<Credit> creditOptional = creditRepository.findById(id);
        if (creditOptional.isEmpty()) {
            throw new RuntimeException("Credit not found!");
        }
        Credit credit = creditOptional.get();

        creditRepository.save(credit);
        creditRepository.deleteById(id);
    }

    @Override
    public List<Credit> findByBankIdAndClientId(Integer bankId, String clientId) {
        Client client = clientService.findByUid(clientId);
        return creditRepository.findByBankIdAndClientId(bankId, client.getId());
    }

    @Override
    public Integer getCount() {
        return creditRepository.countCredits();
    }

    @Override
    public List<Credit> findByBankId(Integer bankId) {
        List<Credit> credits = new LinkedList<>();
        creditRepository.findByBankId(bankId).iterator().forEachRemaining(credits::add);
        return credits;
    }

    @Override
    public List<Credit> findByClientId(String clientId) {
        List<Credit> credits = new LinkedList<>();
        Client client = clientService.findByUid(clientId);
        creditRepository.findByClientId(client.getId()).iterator().forEachRemaining(credits::add);
        return credits;
    }
}
