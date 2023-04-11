package com.awbd.bankingApp.service.impl;

import com.awbd.bankingApp.domain.Card;
import com.awbd.bankingApp.exceptions.ResourceNotFoundException;
import com.awbd.bankingApp.repositories.CardRepository;
import com.awbd.bankingApp.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {
    CardRepository cardRepository;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public List<Card> findAll() {
        List<Card> cards = new LinkedList<>();
        cardRepository.findAll().iterator().forEachRemaining(cards::add);
        return cards;
    }

    @Override
    public Card findById(Integer id) {
        Optional<Card> cardOptional = cardRepository.findById(id);
        if (cardOptional.isEmpty()) {
            throw new ResourceNotFoundException("card " + id + " not found");
        }
        return cardOptional.get();
    }

    @Override
    public Card save(Card card) {
        System.out.println(card);
        return cardRepository.save(card);
    }

    @Override
    public void deleteById(Integer id) {
        Optional<Card> cardOptional = cardRepository.findById(id);
        if (cardOptional.isEmpty()) {
            throw new RuntimeException("Card not found!");
        }
        Card card = cardOptional.get();

        cardRepository.save(card);
        cardRepository.deleteById(id);
    }

    @Override
    public Card findByAccountId(Integer accountId) {
        return cardRepository.findByAccountId(accountId);
    }

    @Override
    public Integer getCount() {
        return cardRepository.countCards();
    }
}
