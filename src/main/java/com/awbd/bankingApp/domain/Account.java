package com.awbd.bankingApp.domain;

import com.awbd.bankingApp.utils.AccountType;
import com.awbd.bankingApp.utils.AccountTypeConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Setter
@Getter
@ToString
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @Size(min = 24, max = 24)
    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Convert(converter = AccountTypeConverter.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType = AccountType.CURRENT;

    @Column(name = "account_value", nullable = false, columnDefinition = "double precision default 0.0")
    private Double accountValue;

    @Transient
    private String username;

    public Account() {}

    public Account(Integer id, Client client, Bank bank, String accountNumber, AccountType accountType, Double accountValue) {
        this.id = id;
        this.client = client;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.accountValue = accountValue;
    }

    public Account(Bank bank, Client client) {
        this.bank = bank;
        this.client = client;
    }

    public Account(Client client, Bank bank, String accountNumber, AccountType accountType, Double accountValue) {
        this.client = client;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.accountValue = accountValue;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
}
