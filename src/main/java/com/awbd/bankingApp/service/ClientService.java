package com.awbd.bankingApp.service;

import com.awbd.bankingApp.domain.Client;

import java.util.List;

public interface ClientService {
    List<Client> findAll();
    Client findById(Integer id);
    Client findByUid(String uid);
    Client save(Client client);
    void deleteById(Integer id);
}
