package com.awbd.bankingApp.dto;

import com.awbd.bankingApp.utils.AccountType;
import com.awbd.bankingApp.utils.AccountTypeConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
public class AccountDTO {
    @NotNull
    private Integer id;

    @NotNull
    private String bankName;

    @NotNull
    private String accountNumber;

    @NotNull
    @Convert(converter = AccountTypeConverter.class)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @NotNull
    private Double accountValue;

    public AccountDTO() {}

    public AccountDTO(Integer id, String bankName, String accountNumber, AccountType accountType, Double accountValue) {
        this.id = id;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.accountValue = accountValue;
    }
}
