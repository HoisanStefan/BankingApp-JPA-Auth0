package com.awbd.bankingApp.service;

import com.awbd.bankingApp.domain.Deposit;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DepositService {
    Page<Deposit> findAll(int page);
    List<Deposit> findAll();
    Deposit findById(Integer id);
    Deposit save(Deposit deposit);
    void deleteById(Integer id);
    Page<Deposit> findAllByOrderByDepositValueAsc(int page);
    Page<Deposit> findAllByOrderByDepositValueDesc(int page);
    Page<Deposit> findAllByOrderByBankNameAsc(int page);
    Page<Deposit> findAllByOrderByBankNameDesc(int page);
    List<Deposit> findAllByOrderByDepositValueAsc();
    List<Deposit> findAllByOrderByDepositValueDesc();
    List<Deposit> findAllByOrderByBankNameAsc();
    List<Deposit> findAllByOrderByBankNameDesc();
    Integer getCount();
}
