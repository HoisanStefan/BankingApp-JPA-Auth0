package com.awbd.bankingApp.controller;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.json.mgmt.users.User;
import com.awbd.bankingApp.domain.Bank;
import com.awbd.bankingApp.domain.Client;
import com.awbd.bankingApp.domain.Contract;
import com.awbd.bankingApp.repositories.ContractRepository;
import com.awbd.bankingApp.service.BankService;
import com.awbd.bankingApp.service.ContractService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;
import org.testng.annotations.BeforeMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("h2")
@RunWith(SpringRunner.class)
@WebMvcTest(ContractController.class)
public class ContractControllerTest {
    @Autowired
    ApplicationContext context;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ContractRepository contractRepository;

    @MockBean
    private ContractService contractService;

    @MockBean
    private BankService bankService;

    @MockBean
    private ManagementAPI managementAPI;

    @MockBean
    private OidcUser principal;

    @MockBean
    private Model model;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationIntegrationTests.class);

    @BeforeMethod
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @WithMockUser(username = "testUser", authorities = {"SCOPE_read:messages"})
    public void testFindById() throws Exception {
        logger.info("Starting testFindById test.");
        Contract contract = new Contract();
        contract.setId(1);
        contract.setClient(new Client("1234"));
        contract.setBank(new Bank("BRD"));
        when(contractService.findById(Mockito.anyInt())).thenReturn(contract);

        User userAPI = Mockito.mock(User.class);
        when(userAPI.getName()).thenReturn("Test User");

        MvcResult mvcResult = mockMvc.perform(get("/contracts/info/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(mvcResult.getResponse().getContentAsString());
        logger.info("Finished testFindById test.");
    }

    @Test
    @WithMockUser(username = "testUser", authorities = {"SCOPE_read:messages"})
    public void testListAll() throws Exception {
        logger.info("Starting testListAll test.");
        // Arrange
        Contract contract1 = new Contract();
        contract1.setId(1);
        Contract contract2 = new Contract();
        contract2.setId(2);
        List<Contract> contracts = Arrays.asList(contract1, contract2);

        User userAPI = Mockito.mock(User.class);
        when(userAPI.getName()).thenReturn("Test User");

        OidcUser principalFromContext = context.getBean(OidcUser.class);
        Mockito.when(principalFromContext.getClaims()).thenReturn(Map.of("sub", "1234"));

        when(contractService.findAll()).thenReturn(contracts);
        when(contractService.findByClientId("5678")).thenReturn(contracts.subList(1, 2));

        when(bankService.findById(1)).thenReturn(new Bank());

        MvcResult mvcResult = mockMvc.perform(get("/contracts/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(mvcResult.getResponse().getContentAsString());
        logger.info("Finished testListAll test.");
    }
}