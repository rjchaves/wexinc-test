package com.wextest.app.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, UUID>, JpaRepository<Transaction, UUID> {
    Page<Transaction> findAllByTransactionDateGreaterThanEqual(LocalDate transactionDate, Pageable pageable);
}
