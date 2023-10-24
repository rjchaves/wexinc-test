package com.wextest.app.transaction;

import com.wextest.app.dto.TransactionDTO;
import com.wextest.app.dto.TransactionPageResponse;
import com.wextest.app.dto.TransactionRequest;
import com.wextest.app.exchange.CountryCurrency;
import com.wextest.app.exchange.ExchangeRateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static com.wextest.app.Constants.CURRENCY_SCALE;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {

    private TransactionRepository transactionRepository;
    private ExchangeRateRepository exchangeRateRepository;

    public void save(TransactionRequest purchasedTransaction) {
        transactionRepository.save(purchasedTransaction.toDomain());
    }

    public TransactionPageResponse getTransactionPageResponse(Integer page, Integer pageSize, String country, String currency, LocalDate fromDate) {
        Page<Transaction> pagedTransactions = transactionRepository.findAllByTransactionDateGreaterThanEqual(fromDate, PageRequest.of(page, pageSize));

        List<TransactionDTO> transactions = TransactionDTO.fromDomain(pagedTransactions.getContent());
        if (StringUtils.isNotBlank(country) && StringUtils.isNotBlank(currency)) {
            transactions.forEach(transaction -> fillConvertedAmount(transaction, country, currency));
        }

        return new TransactionPageResponse(transactions,
                pagedTransactions.getNumber(),
                pagedTransactions.getTotalPages());
    }

    private void fillConvertedAmount(TransactionDTO transaction, String country, String currency) {
        BigDecimal conversionRate = getConversionRate(transaction.getTransactionDate(),
                country, currency);
        BigDecimal convertedAmount = transaction.getAmount().multiply(conversionRate)
                .setScale(CURRENCY_SCALE, RoundingMode.HALF_EVEN);

        transaction.setConvertedAmount(convertedAmount);
    }

    private BigDecimal getConversionRate(LocalDate transactionDate, String country, String currency) {
        LocalDate sixMonths = LocalDate.now().minusMonths(6);

        List<CountryCurrency> orderedExchangeRate = exchangeRateRepository
                .getOrderedExchangeRate(sixMonths, country, currency);

        for (CountryCurrency countryCurrency : orderedExchangeRate) {
            if (transactionDate.isAfter(countryCurrency.recordDate()) ||
                    transactionDate.isEqual(countryCurrency.recordDate())) {
                log.info("using {} convertion rate", countryCurrency.exchangeRate());
                return countryCurrency.exchangeRate();
            }
        }
        throw new NoExchangeRate();
    }
}
