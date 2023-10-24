package com.wextest.app.transaction;

import com.wextest.app.dto.TransactionPageResponse;
import com.wextest.app.dto.TransactionRequest;
import com.wextest.app.exchange.ExchangeRateRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController()
@AllArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private TransactionService transactionService;

    @PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createTransaction(@RequestBody @Valid TransactionRequest purchasedTransaction) {
        transactionService.save(purchasedTransaction);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TransactionPageResponse getTransactions(@RequestParam(defaultValue = "0") Integer page,
                                                   @RequestParam(value = "page-size", defaultValue = "50") Integer pageSize,
                                                   @RequestParam("from-date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                                   @RequestParam(value = "country", required = false) String country,
                                                   @RequestParam(value = "currency", required = false) String currency) {
        return transactionService.getTransactionPageResponse(page, pageSize, country, currency, fromDate);
    }

}
