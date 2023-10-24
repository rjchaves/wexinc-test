package com.wextest.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wextest.app.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@Getter
@Setter
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class TransactionDTO {
    private UUID id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public TransactionDTO(UUID id, String description, LocalDate transactionDate, BigDecimal amount) {
        this.id = id;
        this.description = description;
        this.transactionDate = transactionDate;
        this.amount = amount;
    }

    public static TransactionDTO fromDomain(Transaction transaction) {
        return new TransactionDTO(transaction.getId(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getAmount());
    }

    public static List<TransactionDTO> fromDomain(Collection<Transaction> transactions) {
        return transactions.stream().map(t -> new TransactionDTO(t.getId(),
                t.getDescription(),
                t.getTransactionDate(),
                t.getAmount())).toList();
    }
}
