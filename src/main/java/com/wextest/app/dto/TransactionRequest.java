package com.wextest.app.dto;

import com.wextest.app.transaction.Transaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class TransactionRequest {

    @NotBlank
    @Size(max = 50)
    private String description;

    @NotNull
    private LocalDate transactionDate;

    @NotNull
    private BigDecimal amount;

    public Transaction toDomain() {
        return new Transaction(description, transactionDate, amount);
    }
}
