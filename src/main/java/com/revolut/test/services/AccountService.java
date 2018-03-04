package com.revolut.test.services;

import com.revolut.test.dto.Account;
import com.revolut.test.dto.Transfer;
import com.revolut.test.mapper.AccountMapper;
import com.revolut.test.services.exception.ErrorCode;
import org.mybatis.guice.transactional.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static com.revolut.test.services.util.MoneyHelper.of;
import static com.revolut.test.services.util.ValidationUtil.ccCheck;
import static com.revolut.test.services.util.ValidationUtil.moneyCheck;
import static com.revolut.test.services.util.ValidationUtil.notNull;
import static com.revolut.test.services.util.ValidationUtil.positiveCheck;
import static java.math.BigDecimal.ZERO;

public class AccountService {

    public static final String DOMAIN = "Account";
    private final AccountMapper accountMapper;

    @Inject
    public AccountService(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public List<Account> getAllAccounts() {
        return accountMapper.getAllAccounts();
    }

    public Account getAccount(Long id) {
        return notNull(accountMapper.getAccountById(id), DOMAIN, id);
    }

    @Transactional
    public Account income(Long userId, Account income) {
        positiveCheck(income.getValue(), ErrorCode.INCOME_NOT_POSITIVE);

        Account account = accountMapper.getAccountByUserAndCurrency(userId, income.getCurrencyId());

        if (account != null) {
            of(account).add(income.getValue());
            ccCheck(accountMapper.updateAccountValue(account));
            return account;
        } else {
            return createNewAccount(userId, income.getCurrencyId(), income.getValue());
        }
    }

    private Account createNewAccount(Long userId, Integer currencyId, BigDecimal value) {
        Account newAccount = Account.builder()
                .userId(userId)
                .currencyId(currencyId)
                .value(value).build();
        Long id = accountMapper.createAccount(newAccount);
        newAccount.setId(id);
        return newAccount;
    }

    @Transactional
    public Account withdraw(Long userId, Account withdrawal) {
        positiveCheck(withdrawal.getValue(), ErrorCode.WITHDRAWAL_NOT_POSITIVE);

        Account account = accountMapper.getAccountByUserAndCurrency(userId, withdrawal.getCurrencyId());

        moneyCheck(account, withdrawal.getValue(), withdrawal.getCurrencyId());

        of(account).subtract(withdrawal.getValue());

        ccCheck(accountMapper.updateAccountValue(account));
        return account;
    }

    @Transactional
    public void transfer(Transfer transfer) {
        if (transfer.getUserFrom().equals(transfer.getUserTo())) return;

        Integer currencyId = transfer.getCurrencyId();
        BigDecimal value = transfer.getValue();

        Account from = accountMapper.getAccountByUserAndCurrency(transfer.getUserFrom(), currencyId);
        Account to = accountMapper.getAccountByUserAndCurrency(transfer.getUserTo(), currencyId);

        moneyCheck(from, value, currencyId);

        if (to == null) {
            to = createNewAccount(transfer.getUserTo(), currencyId, ZERO);
        }

        of(from).subtract(value);
        of(to).add(value);

        ccCheck(accountMapper.updateAccountValue(from));
        ccCheck(accountMapper.updateAccountValue(to));
    }
}
