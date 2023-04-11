package com.awbd.bankingApp.service.impl;

import com.awbd.bankingApp.domain.Deposit;
import com.awbd.bankingApp.exceptions.ResourceNotFoundException;
import com.awbd.bankingApp.repositories.DepositRepository;
import com.awbd.bankingApp.service.DepositService;
import com.awbd.bankingApp.utils.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class DepositServiceImpl implements DepositService {
    private final DepositRepository depositRepository;

    public DepositServiceImpl(DepositRepository depositRepository) {
        this.depositRepository = depositRepository;
    }

    @Override
    public Page<Deposit> findAll(int page) {
        return depositRepository.findAll(PageRequest.of(page, Constants.depositsPageSize));
    }

    @Override
    public List<Deposit> findAll() {
        List<Deposit> deposits = new LinkedList<>();
        depositRepository.findAll().iterator().forEachRemaining(deposits::add);
        return deposits;
    }

    @Override
    public Deposit findById(Integer id) {
        return depositRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Deposit not found with id " + id));
    }

    @Override
    public Deposit save(Deposit deposit) {
        return depositRepository.save(deposit);
    }

    @Override
    public void deleteById(Integer id) {
        depositRepository.deleteById(id);
    }

    @Override
    public Page<Deposit> findAllByOrderByDepositValueAsc(int page) {
        return depositRepository.findAllByOrderByDepositValueAsc(PageRequest.of(page, Constants.depositsPageSize));
    }

    @Override
    public Page<Deposit> findAllByOrderByDepositValueDesc(int page) {
        return depositRepository.findAllByOrderByDepositValueDesc(PageRequest.of(page, Constants.depositsPageSize));
    }

    @Override
    public Page<Deposit> findAllByOrderByBankNameAsc(int page) {
        return depositRepository.findAllByOrderByBankNameAsc(PageRequest.of(page, Constants.depositsPageSize));
    }

    @Override
    public Page<Deposit> findAllByOrderByBankNameDesc(int page) {
        return depositRepository.findAllByOrderByBankNameDesc(PageRequest.of(page, Constants.depositsPageSize));
    }

    @Override
    public List<Deposit> findAllByOrderByDepositValueAsc() {
        return depositRepository.findAllByOrderByDepositValueAsc();
    }

    @Override
    public List<Deposit> findAllByOrderByDepositValueDesc() {
        return depositRepository.findAllByOrderByDepositValueDesc();
    }

    @Override
    public List<Deposit> findAllByOrderByBankNameAsc() {
        return depositRepository.findAllByOrderByBankNameAsc();
    }

    @Override
    public List<Deposit> findAllByOrderByBankNameDesc() {
        return depositRepository.findAllByOrderByBankNameDesc();
    }

    @Override
    public Integer getCount() {
        return depositRepository.countDeposits();
    }
}
