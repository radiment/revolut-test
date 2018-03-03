package com.revolut.test.services;

import com.revolut.test.dto.Account;
import com.revolut.test.exception.AccountException;
import com.revolut.test.exception.ErrorCode;
import com.revolut.test.mapper.AccountMapper;
import org.mybatis.guice.transactional.Transactional;

import javax.inject.Inject;
import java.util.List;

import static java.math.BigDecimal.ZERO;

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
        Account account = accountMapper.getAccountById(id);
        if (account == null) {
            throw new AccountException(ErrorCode.NOT_FOUND, "Account", id);
        }
        return account;
    }

    @Transactional
    public Account income(Long userId, Account income) {
        Account account = accountMapper.getAccountByUserIdAndCurrencyId(userId, income.getCurrencyId());

        if (ZERO.compareTo(income.getValue()) >= 0) {
            throw new AccountException(ErrorCode.INCOME_NOT_POSITIVE);
        }

        if (account == null) {
            Account newAccount = Account.builder()
                    .userId(userId)
                    .currencyId(income.getCurrencyId())
                    .value(income.getValue()).build();
            Long id = accountMapper.createAccount(newAccount);
            newAccount.setId(id);
            return newAccount;
        } else {
            account.setValue(account.getValue().add(income.getValue()));
            accountMapper.updateAccountValue(account);
            return account;
        }
    }

    @Transactional
    public Account withdraw(Long userId, Account withdrawal) {
        Account account = accountMapper.getAccountByUserIdAndCurrencyId(userId, withdrawal.getCurrencyId());

        if (account == null || account.getValue().compareTo(withdrawal.getValue()) < 0) {
            throw new AccountException(ErrorCode.NOT_ENOUGH_MONEY, withdrawal.getValue(), withdrawal.getCurrencyId());
        }

        account.setValue(account.getValue().subtract(withdrawal.getValue()));
        accountMapper.updateAccountValue(account);
        return account;
    }
}
