package com.wextest.app.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Meta(
    @JsonProperty("total-count") Integer totalCount,
    @JsonProperty("total-pages") Integer totalPages) {
}
