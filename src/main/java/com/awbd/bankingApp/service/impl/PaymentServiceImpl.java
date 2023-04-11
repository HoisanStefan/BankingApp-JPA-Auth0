package com.awbd.bankingApp.service.impl;

import com.awbd.bankingApp.domain.Payment;
import com.awbd.bankingApp.exceptions.ResourceNotFoundException;
import com.awbd.bankingApp.repositories.PaymentRepository;
import com.awbd.bankingApp.service.PaymentService;
import com.awbd.bankingApp.utils.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Page<Payment> findAll(int page) {
        return paymentRepository.findAll(PageRequest.of(page, Constants.paymentsPageSize));
    }

    @Override
    public List<Payment> findAll() {
        List<Payment> payments = new LinkedList<>();
        paymentRepository.findAll().iterator().forEachRemaining(payments::add);
        return payments;
    }

    @Override
    public Payment findById(Integer id) {
        return paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found with id " + id));
    }

    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public void deleteById(Integer id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public Page<Payment> findAllByOrderByPaidValueAsc(int page) {
        return paymentRepository.findAllByOrderByPaidValueAsc(PageRequest.of(page, Constants.paymentsPageSize));
    }

    @Override
    public Page<Payment> findAllByOrderByPaidValueDesc(int page) {
        return paymentRepository.findAllByOrderByPaidValueDesc(PageRequest.of(page, Constants.paymentsPageSize));
    }

    @Override
    public Page<Payment> findAllByOrderByBankNameAsc(int page) {
        return paymentRepository.findAllByOrderByBankNameAsc(PageRequest.of(page, Constants.paymentsPageSize));
    }

    @Override
    public Page<Payment> findAllByOrderByBankNameDesc(int page) {
        return paymentRepository.findAllByOrderByBankNameDesc(PageRequest.of(page, Constants.paymentsPageSize));
    }

    @Override
    public List<Payment> findAllByOrderByPaidValueAsc() {
        return paymentRepository.findAllByOrderByPaidValueAsc();
    }

    @Override
    public List<Payment> findAllByOrderByPaidValueDesc() {
        return paymentRepository.findAllByOrderByPaidValueDesc();
    }

    @Override
    public List<Payment> findAllByOrderByBankNameAsc() {
        return paymentRepository.findAllByOrderByBankNameAsc();
    }

    @Override
    public List<Payment> findAllByOrderByBankNameDesc() {
        return paymentRepository.findAllByOrderByBankNameDesc();
    }

    @Override
    public Integer getCount() {
        return paymentRepository.countPayments();
    }
}
