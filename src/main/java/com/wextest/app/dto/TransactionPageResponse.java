package com.wextest.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TransactionPageResponse(List<TransactionDTO> transactions,
                                      Integer page,
                                      @JsonProperty("page-size") Integer pageSize) {
}
