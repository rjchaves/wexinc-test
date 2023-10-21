package com.wextest.app;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchasedTransactionDTO {

    @NotBlank
    @Size(max = 50)
    private String description;

    @NotNull
    private LocalDateTime transactionDatetime;

    @NotNull
    private BigDecimal amount;

    public PurchasedTransaction toDomain() {
        return new PurchasedTransaction(description, transactionDatetime, amount);
    }
}
