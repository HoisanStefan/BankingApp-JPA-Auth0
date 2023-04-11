package com.awbd.bankingApp.repositories;

import com.awbd.bankingApp.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends PagingAndSortingRepository<Payment, Integer> {
    Page<Payment> findAllByOrderByPaidValueAsc(Pageable pageable);
    Page<Payment> findAllByOrderByPaidValueDesc(Pageable pageable);
    Page<Payment> findAllByOrderByBankNameAsc(Pageable pageable);
    Page<Payment> findAllByOrderByBankNameDesc(Pageable pageable);
    List<Payment> findAllByOrderByPaidValueAsc();
    List<Payment> findAllByOrderByPaidValueDesc();
    List<Payment> findAllByOrderByBankNameAsc();
    List<Payment> findAllByOrderByBankNameDesc();
    @Query("select count(payment.id) from Payment payment")
    Integer countPayments();
}
