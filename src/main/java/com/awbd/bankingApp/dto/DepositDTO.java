package com.awbd.bankingApp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
public class DepositDTO {
    @NotNull
    private String bankName;

    @NotNull
    private String username;

    @NotNull
    private Integer accountNumber;

    @NotNull
    private Double depositValue;
}