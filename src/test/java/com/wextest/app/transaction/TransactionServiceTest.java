package com.wextest.app.transaction;

import com.wextest.app.dto.TransactionDTO;
import com.wextest.app.exchange.CountryCurrency;
import com.wextest.app.exchange.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    TransactionRepository transactionRepository;
    @Mock
    private ExchangeRateRepository exchangeRateRepository;
    @InjectMocks
    TransactionService transactionService;

    @Test
    void getTransactionPageResponseWithoutConversion() {
        int page = 0;
        int pageSize = 5;
        LocalDate fromDate = LocalDate.of(2023, 1, 30);

        Transaction transaction = new Transaction("description", fromDate, BigDecimal.ONE);
        PageImpl<Transaction> transactions = new PageImpl<>(List.of(transaction));

        when(transactionRepository.findAllByTransactionDateGreaterThanEqual(eq(fromDate), any()))
                .thenReturn(transactions);

        var result = transactionService.getTransactionPageResponse(page, pageSize, null, null, fromDate);

        assertThat(result.transactions()).hasSize(1);
        assertThat(result.transactions().getFirst()).isEqualTo(TransactionDTO.fromDomain(transaction));
    }

    @Test
    void getTransactionPageResponseWithConversion() {
        int page = 0;
        int pageSize = 5;
        LocalDate transactionDate = LocalDate.of(2023, 1, 30);
        String country = "Brazil";
        String currency = "Currency";
        BigDecimal exchangeRate = BigDecimal.valueOf(12.53);
        BigDecimal transactionAmount = BigDecimal.valueOf(1.23);
        BigDecimal convertedValue = BigDecimal.valueOf(15.41);

        Transaction transaction = new Transaction("description", transactionDate, transactionAmount);
        CountryCurrency countryCurrency = new CountryCurrency("countryCurrencyDesc",
                exchangeRate.multiply(BigDecimal.TWO), transactionDate.plusDays(1));
        CountryCurrency countryCurrency2 = new CountryCurrency("countryCurrencyDesc", exchangeRate, transactionDate);
        List<CountryCurrency> countryCurrencies = List.of(countryCurrency, countryCurrency2);

        PageImpl<Transaction> transactions = new PageImpl<>(List.of(transaction));
        when(transactionRepository.findAllByTransactionDateGreaterThanEqual(eq(transactionDate), any()))
                .thenReturn(transactions);
        when(exchangeRateRepository.getOrderedExchangeRate(any(), eq(country), eq(currency)))
                .thenReturn(countryCurrencies);

        var result = transactionService.getTransactionPageResponse(page, pageSize, country, currency, transactionDate);

        assertThat(result.transactions()).hasSize(1);
        assertThat(result.transactions().getFirst().getConvertedAmount())
                .isEqualTo(convertedValue);
    }

    @Test
    void getTransactionPageResponseWithConversionAndNoCurrencyInformation() {
        int page = 0;
        int pageSize = 5;
        LocalDate transactionDate = LocalDate.of(2023, 1, 30);
        String country = "Brazil";
        String currency = "Currency";
        BigDecimal exchangeRate = BigDecimal.valueOf(12.53);
        BigDecimal transactionAmount = BigDecimal.valueOf(1.23);

        Transaction transaction = new Transaction("description", transactionDate, transactionAmount);

        CountryCurrency countryCurrency = new CountryCurrency("countryCurrencyDesc",
                exchangeRate.multiply(BigDecimal.TWO), transactionDate.plusDays(1));
        List<CountryCurrency> countryCurrencies = List.of(countryCurrency);

        PageImpl<Transaction> transactions = new PageImpl<>(List.of(transaction));

        when(transactionRepository.findAllByTransactionDateGreaterThanEqual(eq(transactionDate), any()))
                .thenReturn(transactions);
        when(exchangeRateRepository.getOrderedExchangeRate(any(), eq(country), eq(currency)))
                .thenReturn(countryCurrencies);

        assertThrows(NoExchangeRate.class,
                () -> transactionService.getTransactionPageResponse(page, pageSize, country, currency, transactionDate));
    }
}