package com.revolut.test.dto;

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
public class Transfer {
    @NotNull
    private Long userFrom;
    @NotNull
    private Long userTo;
    @NotNull
    private Integer currencyId;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal value;
}
