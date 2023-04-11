package com.awbd.bankingApp.repositories;

import com.awbd.bankingApp.domain.Deposit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends PagingAndSortingRepository<Deposit, Integer> {
    Page<Deposit> findAllByOrderByDepositValueAsc(Pageable pageable);
    Page<Deposit> findAllByOrderByDepositValueDesc(Pageable pageable);
    Page<Deposit> findAllByOrderByBankNameAsc(Pageable pageable);
    Page<Deposit> findAllByOrderByBankNameDesc(Pageable pageable);
    List<Deposit> findAllByOrderByDepositValueAsc();
    List<Deposit> findAllByOrderByDepositValueDesc();
    List<Deposit> findAllByOrderByBankNameAsc();
    List<Deposit> findAllByOrderByBankNameDesc();
    @Query("select count(deposit.id) from Deposit deposit")
    Integer countDeposits();
}
