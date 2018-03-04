package com.revolut.test.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private static final long DEFAULT_VERSION = 1L;

    private Long id;
    private Long userId;
    @NotNull
    private Integer currencyId;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal value;

    @JsonIgnore
    @Builder.Default
    private long version = DEFAULT_VERSION;

    public Account(Long id, Long userId, Integer currencyId, BigDecimal value) {
        this.id = id;
        this.userId = userId;
        this.value = value;
        this.currencyId = currencyId;
        this.version = DEFAULT_VERSION;
    }
}
