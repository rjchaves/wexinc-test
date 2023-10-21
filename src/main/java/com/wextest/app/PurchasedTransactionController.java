package com.wextest.app;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@AllArgsConstructor
public class PurchasedTransactionController {
    private PurchasedTransactionRepository purchasedTransactionRepository;

    @PostMapping(value = "/transaction", consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createTransaction(@RequestBody @Valid PurchasedTransactionDTO purchasedTransaction) {
        purchasedTransactionRepository.save(purchasedTransaction.toDomain());
    }
}
