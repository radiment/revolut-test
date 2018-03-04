package com.revolut.test.services.util;

import com.revolut.test.dto.Account;

import java.math.BigDecimal;

public class MoneyHelper {
    private Account account;

    private MoneyHelper(Account account) {
        this.account = account;
    }

    public static MoneyHelper of(Account account) {
        return new MoneyHelper(account);
    }

    public MoneyHelper subtract(BigDecimal value) {
        account.setValue(account.getValue().subtract(value));
        return this;
    }

    public MoneyHelper add(BigDecimal value) {
        account.setValue(account.getValue().add(value));
        return this;
    }

    public Account get() {
        return account;
    }
}
