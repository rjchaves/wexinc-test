package com.wextest.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PurchasedTransactionRepository extends JpaRepository<PurchasedTransaction, UUID> {
}
