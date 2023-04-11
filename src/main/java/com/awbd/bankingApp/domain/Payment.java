package com.awbd.bankingApp.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Setter
@Getter
@ToString
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 24, max = 24)
    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "paid_value", nullable = false, columnDefinition = "double precision default 0.0")
    private Double paidValue;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "username", nullable = false)
    private String username;

    @Transient
    private String uid;
}
