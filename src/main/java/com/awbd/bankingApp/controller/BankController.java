package com.awbd.bankingApp.controller;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.awbd.bankingApp.domain.Bank;
import com.awbd.bankingApp.service.BankService;
import com.awbd.bankingApp.utils.UserChecker;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("banks")
public class BankController {
    private final ManagementAPI managementAPI;
    private final BankService bankService;

    public BankController(ManagementAPI managementAPI,BankService bankService) {
        this.bankService = bankService;
        this.managementAPI = managementAPI;
    }

    @RequestMapping("/new")
    public String newBank(Model model) {
        model.addAttribute("bank", new Bank());

        return "banks/bankform";
    }

    @RequestMapping("/update/{id}")
    public String updateBank(@PathVariable Integer id, Model model) {
        Bank bank = bankService.findById(id);
        model.addAttribute("bank", bank);

        return "banks/bankform";
    }

    @GetMapping("/info/{id}")
    public String findById(@PathVariable Integer id, Model model){
        model.addAttribute("bank",
                bankService.findById(id));
        return "banks/info";
    }

    @RequestMapping(value={"/list", "", "/"})
    public ModelAndView banksList(@AuthenticationPrincipal OidcUser principal) throws Auth0Exception {
        ModelAndView modelAndView = new ModelAndView("banks/banks");
        String uid = (String) principal.getClaims().get("sub");
        User userAPI = managementAPI.users().get(uid, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        boolean isAdmin = UserChecker.isAdmin(userAPI.getUserMetadata());
        List<Bank> banks = bankService.findAll();
        modelAndView.addObject("banks", banks);
        modelAndView.addObject("isAdmin", isAdmin);
        return modelAndView;
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable Integer id){
        bankService.deleteById(id);
        return "redirect:/banks/list";
    }

    @PostMapping(value={"", "/"})
    public String saveOrUpdate(@Valid @ModelAttribute Bank bank,
                               BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "banks/bankform";
        }

        Bank savedBank = bankService.save(bank);

        return "redirect:/banks/list" ;
    }
}
