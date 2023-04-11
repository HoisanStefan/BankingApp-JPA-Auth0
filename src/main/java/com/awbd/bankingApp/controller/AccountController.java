package com.awbd.bankingApp.controller;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.awbd.bankingApp.domain.*;
import com.awbd.bankingApp.service.*;
import com.awbd.bankingApp.utils.AccountType;
import com.awbd.bankingApp.utils.CardGenerator;
import com.awbd.bankingApp.utils.IbanGenerator;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("accounts")
public class AccountController {
    private final ManagementAPI managementAPI;
    private final AccountService accountService;
    private final BankService bankService;
    private final ContractService contractService;
    private final CardService cardService;
    private final ClientService clientService;

    public AccountController(ManagementAPI managementAPI, AccountService accountService, BankService bankService, ContractService contractService, CardService cardService, ClientService clientService) {
        this.managementAPI = managementAPI;
        this.accountService = accountService;
        this.bankService = bankService;
        this.contractService = contractService;
        this.cardService = cardService;
        this.clientService = clientService;
    }

    @PostMapping("/accountform")
    public String createForm(@RequestParam("bank_id") String bank_id, HttpSession session, Model model, @AuthenticationPrincipal OidcUser principal) {
        String uid = (String) principal.getClaims().get("sub");
        Optional<Contract> contract = contractService.findByBankIdAndClientId(Integer.valueOf(bank_id), uid);

        if (contract.isPresent()) {
            List<Account> accounts = accountService.findByBankIdAndClientId(Integer.valueOf(bank_id), uid);
            if (accounts.size() > 1) {
                System.out.println("CLIENTUL ARE DEJA 2 TIPURI DE CONTURI");
                model.addAttribute("canAdd", false);
            } else if (accounts.size() == 1) {
                System.out.println("CLIENTUL ARE DOAR UN TIP DE CONT");
                Account account = accounts.get(0);

                Bank accountBank = bankService.findById(Integer.valueOf(bank_id));
                Client accountClient = clientService.findByUid(uid);
                Account newAccount = new Account();
                accountClient.setBanks(new HashSet<>());
                accountBank.setClients(new HashSet<>());
                newAccount.setBank(accountBank);
                newAccount.setClient(accountClient);

                if (account.getAccountType() == AccountType.CURRENT) {
                    model.addAttribute("existingAccount", "CURRENT");
                    newAccount.setAccountType(AccountType.DEPOSIT);
                } else {
                    model.addAttribute("existingAccount", "DEPOSIT");
                    newAccount.setAccountType(AccountType.CURRENT);
                }

                model.addAttribute("account", newAccount);
                model.addAttribute("canAdd", true);
            }
            model.addAttribute("isContractPresent", true);
        } else {
            System.out.println("CLIENTUL NU ARE NICIUN CONT DESCHIS");
            Account account = new Account();
            Bank accountBank = bankService.findById(Integer.valueOf(bank_id));
            Client accountClient = clientService.findByUid(uid);
            accountBank.setClients(new HashSet<>());
            accountClient.setBanks(new HashSet<>());
            account.setBank(accountBank);
            account.setClient(accountClient);

            model.addAttribute("account", account);
            model.addAttribute("isContractPresent", false);
        }

        return "accounts/accountform";
    }

    @PostMapping(value={"new"})
    public String create(HttpSession session, @Valid @ModelAttribute Account account,
                               BindingResult bindingResult, @AuthenticationPrincipal OidcUser principal){


        String uid = (String) principal.getClaims().get("sub");
        Client accountClient = clientService.findByUid(uid);
        account.setClient(new Client(accountClient));
        account.setBank(new Bank(account.getBank().getId()));
        Optional<Contract> contract = contractService.findByBankIdAndClientId(account.getBank().getId(), uid);

        if (contract.isPresent()) {
            System.out.println("EXISTA DEJA CONTRACTUL");
        } else {
            System.out.println("NU EXISTA DEJA CONTRACTUL");
            Bank contractBank = bankService.findById(account.getBank().getId());
            Client contractClient = clientService.findByUid(uid);
            Contract contractNew = new Contract();
            contractNew.setBank(new Bank(contractBank));
            contractNew.setClient(new Client(contractClient));
            contractService.save(contractNew);
            System.out.println("CONTRACTUL A FOST CREAT!");
        }

        Client client = new Client(account.getClient());
        Bank bank = new Bank(account.getBank());
        String ibanCode = IbanGenerator.generateIbanRo();
        account.setClient(client);
        account.setBank(bank);
        account.setAccountValue(0.0);
        account.setAccountNumber(ibanCode);

        Card card = new Card();

        Account savedAccount = accountService.save(account);

        card.setAccount(savedAccount);
        card.setCardNumber(CardGenerator.generateRandomCardNumber());
        card.setCvv(CardGenerator.generateRandomCVV());

        cardService.save(card);

        return "redirect:/banks/list" ;
    }

    @RequestMapping(value={"/list", "", "/"})
    public ModelAndView listAll(HttpSession session, @AuthenticationPrincipal OidcUser principal) throws Auth0Exception {
        ModelAndView modelAndView = new ModelAndView("accounts/accounts");
        String uid = (String) principal.getClaims().get("sub");
        User userAPI = managementAPI.users().get(uid, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        boolean isAdmin = UserChecker.isAdmin(userAPI.getUserMetadata());

        List<Account> accounts;
        if (isAdmin) {
            accounts = accountService.findAll();
            for (Account account : accounts) {
                String uidAccount = account.getClient().getUid();
                User userAPIAccount = managementAPI.users().get(uidAccount, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
                account.setUsername(userAPIAccount.getName());
            }
        } else {
            accounts = accountService.findByClientId(uid);
        }
        modelAndView.addObject("isAdmin", isAdmin);
        modelAndView.addObject("accounts", accounts);

        return modelAndView;
    }

    @GetMapping("/info/{id}")
    public String findById(@PathVariable Integer id, Model model) throws Auth0Exception {
        Account account = accountService.findById(id);

        User userAPI = managementAPI.users().get(account.getClient().getUid(), new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        account.setUsername(userAPI.getName());
        account.setClient(new Client(account.getClient().getUid()));
        account.setBank(new Bank(account.getBank()));
        model.addAttribute("account", account);

        return "accounts/info";
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable Integer id){
        Account account = accountService.findById(id);
        List<Account> accounts = accountService.findByBankIdAndClientId(account.getBank().getId(), account.getClient().getUid());
        if (accounts.size() > 1) {
            accountService.deleteById(id);
        } else if (accounts.size() == 1) {
            Optional<Contract> contract = contractService.findByBankIdAndClientId(account.getBank().getId(), account.getClient().getUid());
            contract.ifPresent(value -> contractService.deleteById(value.getId()));
        }

        return "redirect:/accounts/list";
    }
}
