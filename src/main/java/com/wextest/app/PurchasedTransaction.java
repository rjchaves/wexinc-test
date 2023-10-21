package com.wextest.app;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PurchasedTransaction {
    @Id
    private UUID id;
    private String description;
    private LocalDateTime transactionDatetime;
    private BigDecimal amount;

    public PurchasedTransaction(String description, LocalDateTime transactionDatetime, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.transactionDatetime = transactionDatetime;
        this.amount = amount;
    }
}
