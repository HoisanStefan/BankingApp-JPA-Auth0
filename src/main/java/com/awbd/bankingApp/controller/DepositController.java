package com.awbd.bankingApp.controller;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.awbd.bankingApp.domain.Account;
import com.awbd.bankingApp.domain.Bank;
import com.awbd.bankingApp.domain.Client;
import com.awbd.bankingApp.domain.Deposit;
import com.awbd.bankingApp.dto.AccountDTO;
import com.awbd.bankingApp.dto.DepositDTO;
import com.awbd.bankingApp.service.AccountService;
import com.awbd.bankingApp.service.DepositService;
import com.awbd.bankingApp.utils.Constants;
import com.awbd.bankingApp.utils.UserChecker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("deposits")
public class DepositController {
    private final ManagementAPI managementAPI;
    private final DepositService depositService;
    private final AccountService accountService;

    public DepositController(ManagementAPI managementAPI, DepositService depositService, AccountService accountService) {
        this.managementAPI = managementAPI;
        this.depositService = depositService;
        this.accountService = accountService;
    }

    public Page<Deposit> getDepositsPage(Page<Deposit> allDeposits, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allDeposits.getContent().size());
        List<Deposit> content = allDeposits.getContent().subList(start, end);
        return new PageImpl<>(content, pageable, allDeposits.getTotalElements());
    }

    @RequestMapping(value={"/list", "", "/"})
    public ModelAndView listAll(HttpSession session, @AuthenticationPrincipal OidcUser principal, @RequestParam(defaultValue = "0") int page, @RequestParam(name = "sort_type", defaultValue = "default_sort") String sortType) throws Auth0Exception {
        ModelAndView modelAndView = new ModelAndView("deposits/deposits");
        String uidPrincipal = (String) principal.getClaims().get("sub");
        User userAPISession = managementAPI.users().get(uidPrincipal, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        boolean isAdmin = UserChecker.isAdmin(userAPISession.getUserMetadata());
        modelAndView.addObject("isAdmin", isAdmin);

        if (isAdmin) {
            Page<Deposit> deposits;
            if (sortType.equals("value_asc")) {
                deposits = depositService.findAllByOrderByDepositValueAsc(page);
            } else if (sortType.equals("value_desc")) {
                deposits = depositService.findAllByOrderByDepositValueDesc(page);
            } else if (sortType.equals("bank_name_asc")) {
                deposits = depositService.findAllByOrderByBankNameAsc(page);
            } else if (sortType.equals("bank_name_desc")) {
                deposits = depositService.findAllByOrderByBankNameDesc(page);
            } else {
                deposits = depositService.findAll(page);
            }

            modelAndView.addObject("sortType", sortType);
            modelAndView.addObject("deposits", deposits);
        } else {
            List<Deposit> depositsList;
            if (sortType.equals("value_asc")) {
                depositsList = depositService.findAllByOrderByDepositValueAsc();
            } else if (sortType.equals("value_desc")) {
                depositsList = depositService.findAllByOrderByDepositValueDesc();
            } else if (sortType.equals("bank_name_asc")) {
                depositsList = depositService.findAllByOrderByBankNameAsc();
            } else if (sortType.equals("bank_name_desc")) {
                depositsList = depositService.findAllByOrderByBankNameDesc();
            } else {
                depositsList = depositService.findAll();
            }

            List<Deposit> filteredDeposits = new ArrayList<>();
            for (Deposit deposit : depositsList) {
                if (deposit.getUsername().equals(userAPISession.getName())) {
                    filteredDeposits.add(deposit);
                }
            }

            Page<Deposit> deposits = getDepositsPage(new PageImpl<>(filteredDeposits), page, Constants.depositsPageSize);

            modelAndView.addObject("sortType", sortType);
            modelAndView.addObject("deposits", deposits);
        }

        return modelAndView;
    }

    @PostMapping("/depositform")
    public String createForm(@RequestParam("account_id") String account_id, HttpSession session, Model model, @AuthenticationPrincipal OidcUser principal) throws Auth0Exception {
        Deposit deposit = new Deposit();
        Account account = accountService.findById(Integer.valueOf(account_id));

        Client client = account.getClient();
        Bank bank = account.getBank();
        bank.setClients(new HashSet<>());
        client.setBanks(new HashSet<>());
        User userAPISession = managementAPI.users().get(client.getUid(), new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        model.addAttribute("isAdmin", UserChecker.isAdmin(userAPISession.getUserMetadata()));

        deposit.setBankName(bank.getName());
        deposit.setUsername(userAPISession.getName());
        deposit.setAccountNumber(account_id);
        List<Account> accounts = accountService.findByClientId(client.getUid());
        List<AccountDTO> accountDTOS = new ArrayList<>();

        for (Account accountDeposit : accounts) {
            AccountDTO accountDTO = new AccountDTO(accountDeposit.getId(), accountDeposit.getBank().getName(), accountDeposit.getAccountNumber(), accountDeposit.getAccountType(), accountDeposit.getAccountValue());
            accountDTOS.add(accountDTO);
        }

        model.addAttribute("accounts", accountDTOS);
        model.addAttribute("deposit", deposit);

        return "deposits/depositform";
    }

    @PostMapping(value={"new"})
    public String create(@Valid @ModelAttribute DepositDTO depositDTO, Model model, BindingResult bindingResult, @AuthenticationPrincipal OidcUser principal) throws Auth0Exception {
        System.out.println("NEW DEPOSIT: " + depositDTO);
        Account account = accountService.findById(depositDTO.getAccountNumber());

        String uidPrincipal = (String) principal.getClaims().get("sub");
        User userAPISession = managementAPI.users().get(uidPrincipal, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        boolean isAdmin = UserChecker.isAdmin(userAPISession.getUserMetadata());
        model.addAttribute("isAdmin", isAdmin);

        Deposit deposit = new Deposit();
        deposit.setAccountNumber(account.getAccountNumber());
        deposit.setDepositValue(depositDTO.getDepositValue());
        deposit.setUsername(depositDTO.getUsername());
        deposit.setBankName(depositDTO.getBankName());

        if (bindingResult.hasErrors()){
            return "deposits/depositform";
        }

        double newAccountValue = account.getAccountValue() + deposit.getDepositValue();
        account.setAccountValue(Double.parseDouble(new DecimalFormat("0.00").format(newAccountValue)));        Client client = account.getClient();

        accountService.save(account);
        depositService.save(deposit);

        return "redirect:/deposits/list";
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable Integer id){
        depositService.deleteById(id);
        return "redirect:/deposits/list";
    }

    @GetMapping("/info/{id}")
    public String findById(@PathVariable Integer id, Model model) throws Auth0Exception {
        Deposit deposit = depositService.findById(id);

        model.addAttribute("deposit", deposit);

        return "deposits/info";
    }
}
