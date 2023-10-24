package com.wextest.app.exchange;

import java.util.List;

public record ExchangeRateResponse (
    List<CountryCurrency> data,
    Meta meta) {
}
