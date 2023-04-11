package com.awbd.bankingApp.service;

import com.awbd.bankingApp.domain.Card;

import java.util.List;

public interface CardService {
    List<Card> findAll();
    Card findById(Integer id);
    Card save(Card card);
    void deleteById(Integer id);
    Card findByAccountId(Integer accountId);
    Integer getCount();
}
