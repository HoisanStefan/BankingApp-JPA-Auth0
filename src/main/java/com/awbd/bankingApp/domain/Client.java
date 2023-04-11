package com.awbd.bankingApp.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@ToString
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String uid;

    @ManyToMany
    @JoinTable(name = "contract",
            joinColumns = @JoinColumn(name = "client_id", unique = true),
            inverseJoinColumns = @JoinColumn(name = "bank_id"))
    private Set<Bank> banks = new HashSet<>();

    public Client(String uid) {
        this.uid = uid;
    }

    public Client() {}

    public Client(Client contractClient) {
        this.id = contractClient.id;
        this.uid = contractClient.uid;
    }

    @Override
    public String toString() {
        return "Client [id=" + id + ", uid=" + uid + ", banks=" + banks.stream().map(Bank::getName).collect(Collectors.toList()) + "]";
    }
}
