package com.awbd.bankingApp.controller;

import com.awbd.bankingApp.configuration.Auth0Config;
import com.awbd.bankingApp.configuration.SecurityConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("h2")
@AutoConfigureMockMvc
@Import({Auth0Config.class, SecurityConfig.class})
@SpringBootTest
public class AuthenticationIntegrationTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationIntegrationTests.class);

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        logger.info("MockMvc setup completed.");
    }

    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @Test
    public void testSuccessfulAuthentication() throws Exception {
        logger.info("Starting successful authentication test.");

        MvcResult result = mockMvc.perform(post("/oauth2/authorization/auth0/login")
                        .param("username", "client@gmail.com")
                        .param("password", "Hunedoara1@").with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn();

        // Intercept the redirect
        String redirectUrl = result.getResponse().getRedirectedUrl();

        // Check the redirect URL
        String expectedRedirectUrl = "http://localhost/oauth2/authorization/auth0";
        assertEquals(expectedRedirectUrl, redirectUrl);
        logger.info("Finished successful authentication test.");
    }

    @Test
    public void testUnsuccessfulAuthentication() throws Exception {
        logger.info("Starting unsuccessful authentication test.");

        MvcResult result = mockMvc.perform(post("/oauth2/authorization/auth0/login")
                        .param("username", "invalidUsername")
                        .param("password", "invalidPassword").with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn();

        // Intercept the redirect
        String redirectUrl = result.getResponse().getRedirectedUrl();

        // Check the redirect URL
        String expectedRedirectUrl = "http://localhost/oauth2/authorization/auth0";
        assertEquals(expectedRedirectUrl, redirectUrl);
        logger.info("Finished unsuccessful authentication test.");
    }

    @Test
    public void testLogout() throws Exception {
        logger.info("Starting logout test.");

        MvcResult result = mockMvc.perform(post("/logout").with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection()).andReturn();

        // Intercept the redirect
        URI uri = new URI(Objects.requireNonNull(result.getResponse().getRedirectedUrl()));
        String redirectUrl = uri.getQuery().split("&")[1].split("=")[1];

        // Check the redirect URL
        String expectedRedirectUrl = "http://localhost:8081/";
        assertEquals(expectedRedirectUrl, redirectUrl);

        logger.info("Finished logout test.");
    }
}
