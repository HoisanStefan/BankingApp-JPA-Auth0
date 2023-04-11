package com.awbd.bankingApp.controller;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.awbd.bankingApp.domain.Bank;
import com.awbd.bankingApp.domain.Client;
import com.awbd.bankingApp.domain.Contract;
import com.awbd.bankingApp.service.BankService;
import com.awbd.bankingApp.service.ContractService;
import com.awbd.bankingApp.utils.UserChecker;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("contracts")
public class ContractController {
    private final ManagementAPI managementAPI;
    private final ContractService contractService;
    private final BankService bankService;

    public ContractController(ManagementAPI managementAPI, ContractService contractService, BankService bankService) {
        this.contractService = contractService;
        this.bankService = bankService;
        this.managementAPI = managementAPI;
    }

    @PostMapping("/new")
    public String create(@RequestParam("bank_id") String bank_id, HttpSession session, @AuthenticationPrincipal OidcUser principal) {
        String uid = (String) principal.getClaims().get("sub");
        Optional<Contract> contract = contractService.findByBankIdAndClientId(Integer.valueOf(bank_id), uid);

        if (contract.isPresent()) {
            System.out.println("EXISTA DEJA CONTRACTUL");
        } else {
            System.out.println("NU EXISTA DEJA CONTRACTUL");
            Bank contractBank = bankService.findById(Integer.valueOf(bank_id));
            Contract contractNew = new Contract();
            contractNew.setBank(new Bank(contractBank));
            contractNew.setClient(new Client(uid));
            contractService.save(contractNew);
            System.out.println("CONTRACTUL A FOST CREAT!");
        }

        return "redirect:/banks/list";
    }

    @GetMapping("/info/{id}")
    public String findById(@PathVariable Integer id, Model model) throws Auth0Exception {
        Contract contract = contractService.findById(id);
        User userAPI = managementAPI.users().get(contract.getClient().getUid(), new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();

        contract.setUsername(userAPI.getName());
        model.addAttribute("contract", contract);

        return "contracts/info";
    }

    @RequestMapping(value={"/list", "", "/"})
    public ModelAndView listAll(HttpSession session, @AuthenticationPrincipal OidcUser principal) throws Auth0Exception {
        ModelAndView modelAndView = new ModelAndView("contracts/contracts");
        String uidPrincipal = (String) principal.getClaims().get("sub");
        User userAPISession = managementAPI.users().get(uidPrincipal, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        boolean isAdmin = UserChecker.isAdmin(userAPISession.getUserMetadata());
        modelAndView.addObject("isAdmin", isAdmin);

        if (isAdmin) {
            List<Contract> contracts = contractService.findAll();
            for (Contract contract : contracts) {
                String uid = contract.getClient().getUid();
                User userAPI = managementAPI.users().get(uid, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
                contract.setUsername(userAPI.getName());
            }
            modelAndView.addObject("contracts", contracts);
        } else {
            String uid = (String) principal.getClaims().get("sub");
            List<Contract> contracts = contractService.findByClientId(uid);
            modelAndView.addObject("contracts", contracts);
        }

        return modelAndView;
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable Integer id){
        contractService.deleteById(id);
        return "redirect:/contracts/list";
    }
}
