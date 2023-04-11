package com.awbd.bankingApp.service;

import com.awbd.bankingApp.controller.AuthenticationIntegrationTests;
import com.awbd.bankingApp.domain.Account;
import com.awbd.bankingApp.domain.Bank;
import com.awbd.bankingApp.domain.Client;
import com.awbd.bankingApp.repositories.AccountRepository;
import com.awbd.bankingApp.service.impl.AccountServiceImpl;
import com.awbd.bankingApp.utils.AccountType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@ActiveProfiles("h2")
@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

//    @InjectMocks
//    private CardServiceImpl cardService;
//
//    @InjectMocks
//    private ClientServiceImpl clientService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationIntegrationTests.class);

    @Test
    public void getAccountByIdTest() {
        logger.info("Starting getAccountByIdTest test.");

        Client client = new Client("client1");
        Bank bank = new Bank("bank1");
        Account account = new Account(1, client, bank, "accountNumber", AccountType.CURRENT, 100.00);
        accountRepository.save(account);

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        Account result = accountService.findById(account.getId());

        assertEquals(account, result);
        logger.info("Finished getAccountByIdTest test.");
    }

    @Test
    public void addAccountTest() {
        logger.info("Starting addAccountTest test.");

        Client client = new Client("client1");
        Bank bank = new Bank("bank1");
        Account account = new Account(1, client, bank, "accountNumber", AccountType.CURRENT, 100.00);

        accountService.save(account);

        verify(accountRepository, times(1)).save(account);
        logger.info("Finished addAccountTest test.");
    }
}
