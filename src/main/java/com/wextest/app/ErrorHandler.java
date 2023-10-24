package com.wextest.app;

import com.wextest.app.transaction.NoExchangeRate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NoExchangeRate.class)
    @ResponseBody
    public ErrorResponse noExchangeError(NoExchangeRate noExchangeRate) {
        return new ErrorResponse("No exchange rate available");
    }
}
