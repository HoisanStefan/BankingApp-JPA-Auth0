package com.awbd.bankingApp.controller;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.awbd.bankingApp.domain.Account;
import com.awbd.bankingApp.domain.Card;
import com.awbd.bankingApp.service.AccountService;
import com.awbd.bankingApp.service.CardService;
import com.awbd.bankingApp.service.ContractService;
import com.awbd.bankingApp.utils.UserChecker;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("cards")
public class CardController {
    private final ManagementAPI managementAPI;
    private final AccountService accountService;
    private final CardService cardService;
    private final ContractService contractService;

    public CardController(ManagementAPI managementAPI, AccountService accountService, CardService cardService, ContractService contractService) {
        this.managementAPI = managementAPI;
        this.accountService = accountService;
        this.cardService = cardService;
        this.contractService = contractService;
    }

    @RequestMapping(value={"/list", "", "/"})
    public ModelAndView listAll(HttpSession session, @AuthenticationPrincipal OidcUser principal) throws Auth0Exception {
        ModelAndView modelAndView = new ModelAndView("cards/cards");
        String uidPrincipal = (String) principal.getClaims().get("sub");
        User userAPISession = managementAPI.users().get(uidPrincipal, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        boolean isAdmin = UserChecker.isAdmin(userAPISession.getUserMetadata());
        modelAndView.addObject("isAdmin", isAdmin);
        if (isAdmin) {
            List<Card> cards = cardService.findAll();
            for (Card card : cards) {
                String uid = card.getAccount().getClient().getUid();
                User userAPI = managementAPI.users().get(uid, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
                card.setUsername(userAPI.getName());
            }
            modelAndView.addObject("cards", cards);
        } else {
            String uid = (String) principal.getClaims().get("sub");
            List<Card> cards = new ArrayList<>();
            List<Account> accounts = accountService.findByClientId(uid);
            for (Account account : accounts) {
                Card card = cardService.findByAccountId(account.getId());
                cards.add(card);
            }
            modelAndView.addObject("cards", cards);
        }

        return modelAndView;
    }

    @GetMapping("/info/{id}")
    public String findById(@PathVariable Integer id, Model model) throws Auth0Exception {
        Card card = cardService.findById(id);

        User userAPI = managementAPI.users().get(card.getAccount().getClient().getUid(), new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        card.setUsername(userAPI.getName());
        model.addAttribute("card", card);

        return "cards/info";
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable Integer id){
        Card card = cardService.findById(id);

        // Note: This will also delete the associated card:
        contractService.deleteAccount(card.getAccount());
        return "redirect:/cards/list";
    }
}
