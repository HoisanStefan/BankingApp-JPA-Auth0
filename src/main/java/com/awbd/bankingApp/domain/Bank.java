package com.awbd.bankingApp.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@ToString
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    @ManyToMany(mappedBy = "banks",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Client> clients = new HashSet<>();

    public Bank() {}

    public Bank(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Bank(Integer id) {
        this.id = id;
    }

    public Bank(String name) {
        this.name = name;
    }

    public Bank(Bank contractBank) {
        this.id = contractBank.id;
        this.name = contractBank.name;
    }

    @Override
    public String toString() {
        return "Bank [id=" + id + ", name=" + name + ", clients=" + clients.stream().map(Client::getUid).collect(Collectors.toList()) + "]";
    }
}
