package com.awbd.bankingApp.controller;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.awbd.bankingApp.domain.Account;
import com.awbd.bankingApp.domain.Bank;
import com.awbd.bankingApp.domain.Client;
import com.awbd.bankingApp.domain.Credit;
import com.awbd.bankingApp.service.AccountService;
import com.awbd.bankingApp.service.BankService;
import com.awbd.bankingApp.service.ClientService;
import com.awbd.bankingApp.service.CreditService;
import com.awbd.bankingApp.utils.CreditState;
import com.awbd.bankingApp.utils.UserChecker;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("credits")
public class CreditController {
    private final ManagementAPI managementAPI;
    private final CreditService creditService;
    private final BankService bankService;
    private final AccountService accountService;
    private final ClientService clientService;

    public CreditController(ManagementAPI managementAPI, CreditService creditService, BankService bankService, AccountService accountService, ClientService clientService) {
        this.creditService = creditService;
        this.bankService = bankService;
        this.managementAPI = managementAPI;
        this.accountService = accountService;
        this.clientService = clientService;
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable Integer id){
        creditService.deleteById(id);
        return "redirect:/credits/list";
    }

    @RequestMapping(value={"/list", "", "/"})
    public ModelAndView listAll(HttpSession session, @AuthenticationPrincipal OidcUser principal) throws Auth0Exception {
        ModelAndView modelAndView = new ModelAndView("credits/credits");
        String uidPrincipal = (String) principal.getClaims().get("sub");
        User userAPISession = managementAPI.users().get(uidPrincipal, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        boolean isAdmin = UserChecker.isAdmin(userAPISession.getUserMetadata());
        modelAndView.addObject("isAdmin", isAdmin);

        if (isAdmin) {
            List<Credit> credits = creditService.findAll();
            for (Credit credit : credits) {
                if (credit.getClient() != null) {
                    String uid = credit.getClient().getUid();
                    User userAPI = managementAPI.users().get(uid, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
                    credit.setUsername(userAPI.getName());
                } else {
                    credit.setUsername("-");
                }
            }
            modelAndView.addObject("credits", credits);
        } else {
            String uid = (String) principal.getClaims().get("sub");
            List<Credit> credits = creditService.findAll();
            List<Credit> creditsBank = new ArrayList<>();
            List<Credit> creditsClient = new ArrayList<>();
            for (Credit credit : credits) {
                if (credit.getClient() != null) {
                    if (credit.getClient().getUid().equals(uid)) {
                        creditsClient.add(credit);
                    }
                } else {
                    creditsBank.add(credit);
                }
            }
            modelAndView.addObject("credit", new Credit());
            modelAndView.addObject("creditsBank", creditsBank);
            modelAndView.addObject("creditsClient", creditsClient);
        }

        return modelAndView;
    }

    @GetMapping("/info/{id}")
    public String findById(@PathVariable Integer id, Model model) throws Auth0Exception {
        Credit credit = creditService.findById(id);
        if (credit.getClient() != null) {
            User userAPI = managementAPI.users().get(credit.getClient().getUid(), new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
            credit.setUsername(userAPI.getName());
        } else {
            credit.setUsername("-");
        }

        model.addAttribute("credit", credit);

        return "credits/info";
    }

    @PostMapping("/creditform")
    public String createForm(@RequestParam("bank_id") String bank_id, HttpSession session, Model model) {
        Credit credit = new Credit();
        Bank bank = bankService.findById(Integer.valueOf(bank_id));
        bank.setClients(new HashSet<>());

        credit.setBank(bank);
        credit.setCreditState(CreditState.NEW);

        model.addAttribute("credit", credit);

        return "credits/creditform";
    }

    @PostMapping(value={"new"})
    public String create(@Valid @ModelAttribute Credit credit, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "credits/creditform";
        }

        credit.setBank(new Bank(credit.getBank()));
        creditService.save(credit);

        return "redirect:/credits/list";
    }

    @PostMapping(value={"loan"})
    public String chooseCredit(@Valid @ModelAttribute Credit credit, @AuthenticationPrincipal OidcUser principal, BindingResult bindingResult, HttpSession session, Model model) throws Auth0Exception {
        if (bindingResult.hasErrors()){
            return "credits/creditform";
        }

        credit.setBank(credit.getBank());
        String uid = (String) principal.getClaims().get("sub");
        User userAPI = managementAPI.users().get(uid, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();

        List<Account> accounts = accountService.findByBankIdAndClientId(credit.getBank().getId(), uid);
        List<Credit> credits = creditService.findByBankIdAndClientId(credit.getBank().getId(), uid);
        credits = credits.stream().filter(c -> c.getCreditState() == CreditState.UNPAID)
                .collect(Collectors.toList());

        if (credits.size() > 0) {
            String errorMessage = "CLIENTUL ARE DEJA UN CREDIT DESCHIS LA ACEASTA BANCA";
            System.out.println(errorMessage);

            model.addAttribute("isAdmin", UserChecker.isAdmin(userAPI.getUserMetadata()));
            model.addAttribute("errorMessage", errorMessage);
            return "credits/errorpage";
        } else {
            if (accounts.size() > 0) {
                Client client = clientService.findByUid(uid);
                credit.setClient(new Client(client));
                credit.setBank(new Bank(credit.getBank()));
                credit.setCreditState(CreditState.UNPAID);
                credit.setAmountTotal(credit.getAmountTotal());
                credit.setAmountPaid(0.0);
                creditService.save(credit);
            } else {
                String errorMessage = "CLIENTUL NU ARE UN CONT DESCHIS LA ACEASTA BANCA";
                System.out.println(errorMessage);
                model.addAttribute("errorMessage", errorMessage);
                model.addAttribute("isAdmin", UserChecker.isAdmin(userAPI.getUserMetadata()));
                return "credits/errorpage";
            }
        }

        return "redirect:/credits/list";
    }
}
