package com.wextest.app.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.management.timer.Timer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.wextest.app.Constants.EXCHANGE_CACHE_NAME;

@Repository
@Slf4j
public class ExchangeRateRepository {
    private final RestTemplate restTemplate;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final RetryTemplate retryTemplate;
    private final CacheManager cacheManager;

    public ExchangeRateRepository(RestTemplate restTemplate, CacheManager cacheManager) {
        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
        this.retryTemplate = RetryTemplate.builder()
                .maxAttempts(3)
                .fixedBackoff(1000)
                .retryOn(HttpServerErrorException.class)
                .build();
    }

    /**
     * This method will bring all the six months of exchange rates and cache it. While this can increase the response time
     * of the first request it will reduce the subsequents and will also protect the application against possible
     * unavailabilities
     * */
    @Cacheable(EXCHANGE_CACHE_NAME)
    public List<CountryCurrency> getOrderedExchangeRate(LocalDate greaterThan, String country, String currency) {
        ExchangeRateResponse exchangeRateFromExternalSource = getExchangeRateFromExternalSource(greaterThan, country, currency, 1);
        List<CountryCurrency> result = exchangeRateFromExternalSource.data();

        Integer numberOfPages = exchangeRateFromExternalSource.meta().totalPages();
        if(numberOfPages == 1) {
            return result;
        }
        List<CountryCurrency> list = IntStream.rangeClosed(2, numberOfPages)
                .mapToObj((page) -> getExchangeRateFromExternalSource(greaterThan, country, currency, page))
                .flatMap((e) -> e.data().stream())
                .toList();
        result.addAll(list);

        return result;
    }

    @Scheduled(fixedRate = Timer.ONE_HOUR * 6)
    public void cacheEvict () {
        log.info("Clearing {} cache", EXCHANGE_CACHE_NAME);
        Optional.ofNullable(cacheManager.getCache(EXCHANGE_CACHE_NAME)).ifPresent(Cache::clear);
    }

    private ExchangeRateResponse getExchangeRateFromExternalSource(LocalDate greaterThan, String country, String currency, Integer page) {
        String filter = new StringBuilder()
                .append("record_date:gte:").append(dateTimeFormatter.format(greaterThan)).append(",")
                .append("country:eq:").append(country).append(",")
                .append("currency:eq:").append(currency).toString();

        //TODO externalize this URL
        String uriString = UriComponentsBuilder.fromHttpUrl("https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange")
                .queryParam("fields", "country_currency_desc,exchange_rate,record_date")
                .queryParam("filter", filter)
                .queryParam("sort", "record_date")
                .queryParam("page[number]", page)
                .queryParam("page[size]", 1)
                .build()
                .toUriString();

        return retryTemplate.execute((ctx) -> exchange(uriString));
    }

    private ExchangeRateResponse exchange(String uriString) {
        ResponseEntity<ExchangeRateResponse> exchange = restTemplate.exchange(uriString,
                HttpMethod.GET,
                null,
                ExchangeRateResponse.class);
        if (exchange.getStatusCode().isError()) {
            throw new HttpServerErrorException(exchange.getStatusCode());
        }
        return exchange.getBody();
    }

}
