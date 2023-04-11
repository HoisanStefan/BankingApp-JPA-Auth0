package com.awbd.bankingApp.service.impl;

import com.awbd.bankingApp.domain.Client;
import com.awbd.bankingApp.exceptions.ResourceNotFoundException;
import com.awbd.bankingApp.repositories.ClientRepository;
import com.awbd.bankingApp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {
    ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = new LinkedList<>();
        clientRepository.findAll().iterator().forEachRemaining(clients::add);
        return clients;
    }

    @Override
    public Client findById(Integer id) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (clientOptional.isEmpty()) {
            throw new ResourceNotFoundException("client " + id + " not found");
        }
        return clientOptional.get();
    }

    @Override
    public Client findByUid(String uid) {
        Optional<Client> clientOptional = clientRepository.findByUid(uid);
        if (clientOptional.isEmpty()) {
            throw new ResourceNotFoundException("client " + uid + " not found");
        }
        return clientOptional.get();
    }

    @Override
    public Client save(Client client) {
        System.out.println(client);
        return clientRepository.save(client);
    }

    @Override
    public void deleteById(Integer id) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (clientOptional.isEmpty()) {
            throw new RuntimeException("Client not found!");
        }
        Client client = clientOptional.get();
        clientRepository.save(client);
        clientRepository.deleteById(id);
    }
}
