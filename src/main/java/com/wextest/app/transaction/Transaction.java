package com.wextest.app.transaction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

import static com.wextest.app.Constants.CURRENCY_SCALE;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @Id
    private UUID id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal amount;

    public Transaction(String description, LocalDate transactionDate, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.transactionDate = transactionDate;
        this.amount = amount.setScale(CURRENCY_SCALE, RoundingMode.HALF_EVEN);
    }
}
