package com.revolut.test.services.util;

import com.revolut.test.dto.Account;
import com.revolut.test.services.exception.AccountException;
import com.revolut.test.services.exception.ErrorCode;

import java.io.Serializable;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

public class ValidationUtil {

    private ValidationUtil() {}

    public static void ccCheck(boolean updated) {
        if (!updated) {
            throw new AccountException(ErrorCode.CONCURRENT_CHANGE);
        }
    }

    public static void positiveCheck(BigDecimal value, ErrorCode errorCode) {
        if (ZERO.compareTo(value) >= 0) {
            throw new AccountException(errorCode);
        }
    }

    public static <T> T notNull(T value, String domain, Serializable id) {
        if (value == null) {
            throw new AccountException(ErrorCode.NOT_FOUND, domain, id);
        }
        return value;
    }

    public static void moneyCheck(Account account, BigDecimal value, Integer currencyId) {
        if (account == null || account.getValue().compareTo(value) < 0) {
            throw new AccountException(ErrorCode.NOT_ENOUGH_MONEY, value, currencyId);
        }
    }
}
