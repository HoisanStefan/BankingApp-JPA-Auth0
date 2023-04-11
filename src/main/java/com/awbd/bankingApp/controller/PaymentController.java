package com.awbd.bankingApp.controller;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.awbd.bankingApp.domain.*;
import com.awbd.bankingApp.dto.AccountDTO;
import com.awbd.bankingApp.dto.PaymentDTO;
import com.awbd.bankingApp.service.AccountService;
import com.awbd.bankingApp.service.CreditService;
import com.awbd.bankingApp.service.PaymentService;
import com.awbd.bankingApp.utils.Constants;
import com.awbd.bankingApp.utils.CreditState;
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
@RequestMapping("payments")
public class PaymentController {
    private final ManagementAPI managementAPI;
    private final PaymentService paymentService;
    private final CreditService creditService;
    private final AccountService accountService;

    public PaymentController(ManagementAPI managementAPI, PaymentService paymentService, CreditService creditService, AccountService accountService) {
        this.managementAPI = managementAPI;
        this.paymentService = paymentService;
        this.creditService = creditService;
        this.accountService = accountService;
    }

    public Page<Payment> getPaymentsPage(Page<Payment> allPayments, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allPayments.getContent().size());
        List<Payment> content = allPayments.getContent().subList(start, end);
        return new PageImpl<>(content, pageable, allPayments.getTotalElements());
    }

    @RequestMapping(value={"/list", "", "/"})
    public ModelAndView listAll(HttpSession session, @AuthenticationPrincipal OidcUser principal, @RequestParam(defaultValue = "0") int page, @RequestParam(name = "sort_type", defaultValue = "default_sort") String sortType) throws Auth0Exception {
        ModelAndView modelAndView = new ModelAndView("payments/payments");
        String uidPrincipal = (String) principal.getClaims().get("sub");
        User userAPISession = managementAPI.users().get(uidPrincipal, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        boolean isAdmin = UserChecker.isAdmin(userAPISession.getUserMetadata());
        modelAndView.addObject("isAdmin", isAdmin);

        if (isAdmin) {
            Page<Payment> payments;
            if (sortType.equals("value_asc")) {
                payments = paymentService.findAllByOrderByPaidValueAsc(page);
            } else if (sortType.equals("value_desc")) {
                payments = paymentService.findAllByOrderByPaidValueDesc(page);
            } else if (sortType.equals("bank_name_asc")) {
                payments = paymentService.findAllByOrderByBankNameAsc(page);
            } else if (sortType.equals("bank_name_desc")) {
                payments = paymentService.findAllByOrderByBankNameDesc(page);
            } else {
                payments = paymentService.findAll(page);
            }

            modelAndView.addObject("sortType", sortType);
            modelAndView.addObject("payments", payments);
        } else {
            List<Payment> paymentsList;
            if (sortType.equals("value_asc")) {
                paymentsList = paymentService.findAllByOrderByPaidValueAsc();
            } else if (sortType.equals("value_desc")) {
                paymentsList = paymentService.findAllByOrderByPaidValueDesc();
            } else if (sortType.equals("bank_name_asc")) {
                paymentsList = paymentService.findAllByOrderByBankNameAsc();
            } else if (sortType.equals("bank_name_desc")) {
                paymentsList = paymentService.findAllByOrderByBankNameDesc();
            } else {
                paymentsList = paymentService.findAll();
            }

            List<Payment> filteredPayments = new ArrayList<>();
            for (Payment payment : paymentsList) {
                if (payment.getUsername().equals(userAPISession.getName())) {
                    filteredPayments.add(payment);
                }
            }

            Page<Payment> payments = getPaymentsPage(new PageImpl<>(filteredPayments), page, Constants.paymentsPageSize);

            modelAndView.addObject("sortType", sortType);
            modelAndView.addObject("payments", payments);
        }

        return modelAndView;
    }

    @PostMapping("/payform")
    public String createForm(@RequestParam("credit_id") String credit_id, HttpSession session, Model model, @AuthenticationPrincipal OidcUser principal) throws Auth0Exception {
        PaymentDTO payment = new PaymentDTO();
        Credit credit = creditService.findById(Integer.valueOf(credit_id));

        Client client = credit.getClient();
        Bank bank = credit.getBank();
        bank.setClients(new HashSet<>());
        client.setBanks(new HashSet<>());
        User userAPISession = managementAPI.users().get(client.getUid(), new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        model.addAttribute("isAdmin", UserChecker.isAdmin(userAPISession.getUserMetadata()));

        if (credit.getCreditState() == CreditState.PAID) {
            String errorMessage = "CLIENTUL A PLATIT DEJA SUMA TOTALA PENTRU CREDIT";
            System.out.println(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            return "payments/errorpage";
        }

        payment.setBankName(bank.getName());
        payment.setCreditId(credit.getId());
        payment.setUsername(userAPISession.getName());
        List<Account> accounts = accountService.findByClientId(client.getUid());
        if (accounts.size() < 1) {
            String errorMessage = "CLIENTUL NU ARE NICIUN CONT DESCHIS";
            System.out.println(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            return "payments/errorpage";
        } else {
            List<AccountDTO> accountDTOS = new ArrayList<>();
            for (Account account : accounts) {
                AccountDTO accountDTO = new AccountDTO(account.getId(), account.getBank().getName(), account.getAccountNumber(), account.getAccountType(), account.getAccountValue());
                accountDTOS.add(accountDTO);
            }
            model.addAttribute("accounts", accountDTOS);
        }

        model.addAttribute("payment", payment);

        return "payments/payform";
    }

    @PostMapping(value={"new"})
    public String create(@Valid @ModelAttribute PaymentDTO paymentDTO, Model model, BindingResult bindingResult, @AuthenticationPrincipal OidcUser principal) throws Auth0Exception {
        System.out.println("NEW PAY: " + paymentDTO);

        Account account = accountService.findById(paymentDTO.getAccountNumber());
        Credit credit = creditService.findById(paymentDTO.getCreditId());

        String uidPrincipal = (String) principal.getClaims().get("sub");
        User userAPISession = managementAPI.users().get(uidPrincipal, new UserFilter().withTotals(false).withFields("user_metadata,email,name", true)).execute();
        boolean isAdmin = UserChecker.isAdmin(userAPISession.getUserMetadata());
        model.addAttribute("isAdmin", isAdmin);

        Payment payment = new Payment();
        payment.setAccountNumber(account.getAccountNumber());
        payment.setPaidValue(paymentDTO.getPaidValue());
        payment.setUsername(paymentDTO.getUsername());
        payment.setBankName(paymentDTO.getBankName());
        payment.setUid(uidPrincipal);

        if (account.getAccountValue() < payment.getPaidValue()) {
            String errorMessage = "CONTUL NU DISPUNE DE SUFICIENTE FONDURI PENTRU A EFECTUA PLATA!";
            System.out.println(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            return "payments/errorpage";
        }

        if (bindingResult.hasErrors()){
            return "payments/payform";
        }

        Double leftToPay = credit.getAmountTotal() - credit.getAmountPaid();

        if (leftToPay > payment.getPaidValue()) {
            double newAccountValue = account.getAccountValue() - payment.getPaidValue();
            double newAmountPaid = credit.getAmountPaid() + payment.getPaidValue();

            account.setAccountValue(Double.parseDouble(new DecimalFormat("0.00").format(newAccountValue)));
            credit.setAmountPaid(Double.parseDouble(new DecimalFormat("0.00").format(newAmountPaid)));
        } else {
            double newAccountValue = account.getAccountValue() - leftToPay;
            double newAmountPaid = credit.getAmountPaid() + leftToPay;

            account.setAccountValue(Double.parseDouble(new DecimalFormat("0.00").format(newAccountValue)));
            credit.setAmountPaid(Double.parseDouble(new DecimalFormat("0.00").format(newAmountPaid)));
            credit.setCreditState(CreditState.PAID);
            payment.setPaidValue(leftToPay);
        }

        accountService.save(account);
        creditService.save(credit);
        paymentService.save(payment);

        return "redirect:/payments/list";
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable Integer id){
        paymentService.deleteById(id);
        return "redirect:/payments/list";
    }

    @GetMapping("/info/{id}")
    public String findById(@PathVariable Integer id, Model model) throws Auth0Exception {
        Payment payment = paymentService.findById(id);

        model.addAttribute("payment", payment);

        return "payments/info";
    }
}
