package com.awbd.bankingApp.domain;

import com.awbd.bankingApp.utils.CreditState;
import com.awbd.bankingApp.utils.CreditStateConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Setter
@Getter
@ToString
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @Column(name = "amount_total", nullable = false, columnDefinition = "double precision default 0.0")
    private Double amountTotal;

    @Column(name = "amount_paid", columnDefinition = "double precision default 0.0")
    private Double amountPaid = 0.0;

    @Convert(converter = CreditStateConverter.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "credit_state", nullable = false)
    private CreditState creditState = CreditState.NEW;

    @Transient
    private String username = "-";
}
