package com.revolut.test.services;

import com.revolut.test.dto.Account;
import com.revolut.test.mapper.AccountMapper;

import javax.inject.Inject;
import java.util.List;

public class AccountService {

    private final AccountMapper accountMapper;

    @Inject
    public AccountService(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public List<Account> getAllAccounts() {
        return accountMapper.getAllAccounts();
    }

    public Account getAccount(Long id) {
        return accountMapper.getAccountById(id);
    }

    public Account income(Long userId, Account account) {
        Account created = accountMapper.getAccountByUserIdAndCurrencyId(userId, account.getCurrencyId());
        if (created == null) {
            Account newAccount = Account.builder()
                    .userId(userId)
                    .currencyId(account.getCurrencyId())
                    .value(account.getValue()).build();
            Long id = accountMapper.createAccount(newAccount);
            return accountMapper.getAccountById(id);
        } else {
            created.setValue(account.getValue());
            accountMapper.updateAccountValue(created);
            return created;
        }
    }
}
