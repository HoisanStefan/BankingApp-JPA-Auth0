package com.awbd.bankingApp.service;

import com.awbd.bankingApp.domain.Payment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaymentService {
    Page<Payment> findAll(int page);
    List<Payment> findAll();
    Payment findById(Integer id);
    Payment save(Payment payment);
    void deleteById(Integer id);
    Page<Payment> findAllByOrderByPaidValueAsc(int page);
    Page<Payment> findAllByOrderByPaidValueDesc(int page);
    Page<Payment> findAllByOrderByBankNameAsc(int page);
    Page<Payment> findAllByOrderByBankNameDesc(int page);
    List<Payment> findAllByOrderByPaidValueAsc();
    List<Payment> findAllByOrderByPaidValueDesc();
    List<Payment> findAllByOrderByBankNameAsc();
    List<Payment> findAllByOrderByBankNameDesc();
    Integer getCount();
}
