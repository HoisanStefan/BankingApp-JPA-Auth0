package com.awbd.bankingApp;

import com.awbd.bankingApp.controller.AuthenticationIntegrationTests;
import com.awbd.bankingApp.controller.ContractControllerTest;
import com.awbd.bankingApp.service.AccountServiceTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2")
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AuthenticationIntegrationTests.class,
        ContractControllerTest.class,
        AccountServiceTest.class,
})
public class BankingAppApplicationTests {
    @Test
    public void contextLoads() {}
}
