package com.revolut.test.services

import com.revolut.test.dto.Account
import com.revolut.test.dto.Transfer
import com.revolut.test.services.exception.AccountException
import com.revolut.test.services.exception.ErrorCode
import com.revolut.test.mapper.AccountMapper
import spock.lang.Specification

class AccountServiceTest extends Specification {

    public static final int CURRENCY_ID = 810
    public static final int USER1 = 1
    public static final int USER2 = 2

    AccountService accountService
    AccountMapper accountMapper

    def setup() {
        accountMapper = Mock(AccountMapper)
        accountService = new AccountService(accountMapper)
    }

    def "getAllAccounts returns all accounts"() {
        given: "list with 2 accounts of one user and one for another"
        def accounts = [
                new Account(1, USER1, CURRENCY_ID, 200),
                new Account(2, USER1, 800, 300),
                new Account(3, USER2, CURRENCY_ID, 100)
        ]
        and: "account mapper always return this accounts on getAllAccounts"
        accountMapper.getAllAccounts() >> accounts

        when: "we ask service all accounts"
        def result = accountService.getAllAccounts()

        then: "result contains all accounts"
        result == accounts
    }

    def "getAccount returns exact account"() {
        given: "some account"
        def account = new Account(1, 1, CURRENCY_ID, 200)

        and: "accountMapper return this account by his id"
        accountMapper.getAccountById(account.getId()) >> account

        when: "request service with defined id"
        def result = accountService.getAccount(1)

        then: "result equals to defined account"
        result == account
    }

    def "getAccount returns exception when wrong id"() {
        given: "accountMapper return null by defined id"
        accountMapper.getAccountById(1) >> null

        when: "request service with defined id"
        accountService.getAccount(1)

        then: "throw Exception with error NOT_FOUND"
        def ex = thrown(AccountException)
        ex.errorMessage.serviceError == ErrorCode.NOT_FOUND
    }

    def "income throws exception when requested negative income"() {
        given: "request account with negative value"
        def income = new Account(value: -100, currencyId: CURRENCY_ID)

        when: "request service to add income with some userId and defined account"
        accountService.income(1, income)

        then: "throws exception with errorCode INCOME_NOT_POSITIVE"
        def ex = thrown(AccountException)
        ex.errorMessage.serviceError == ErrorCode.INCOME_NOT_POSITIVE
    }

    def "income create new Account if there are no for such userId currencyId"() {
        given:
        def userId = 1
        def accountId = 10
        def income = new Account(value: 100, currencyId: CURRENCY_ID)

        and:
        1 * accountMapper.createAccount(_ as Account) >> { Account account -> account.setId(accountId); return 1 }

        when:
        def result = accountService.income(userId, income)

        then:
        result == new Account(accountId, userId, income.currencyId, income.value)
    }

    def "income add money to the existed account"() {
        given:
        def userId = USER1
        def existed = new Account(1, userId, CURRENCY_ID, 100)
        def income = new Account(value: 50, currencyId: CURRENCY_ID)
        def expected = new Account(1, userId, CURRENCY_ID, 150)

        and:
        accountMapper.getAccountByUserAndCurrency(userId, CURRENCY_ID) >> existed

        when:
        def result = accountService.income(userId, income)

        then:
        result == expected
        1 * accountMapper.updateAccountValue(expected) >> true
    }

    def "withdraw throws exception when account not found or not enough money"(Account existed) {
        given:
        def withdrawal = new Account(value: 200, currencyId: CURRENCY_ID)

        and:
        accountMapper.getAccountByUserAndCurrency(USER1, CURRENCY_ID) >> existed

        when:
        accountService.withdraw(USER1, withdrawal)

        then:
        def ex = thrown(AccountException)
        ex.errorMessage.serviceError == ErrorCode.NOT_ENOUGH_MONEY

        where:
        existed << [null,
                    new Account(1, USER1, CURRENCY_ID, 100),
                    new Account(1, USER1, CURRENCY_ID, 199.99)]

    }

    def "withdraw subtracts value if enough money"(BigDecimal withdrawalValue, BigDecimal expectedValue) {
        given:
        def existed = new Account(1, USER1, CURRENCY_ID, 200)
        def expected = new Account(1, USER1, CURRENCY_ID, expectedValue)
        def withdrawal = new Account(value: withdrawalValue, currencyId: CURRENCY_ID)

        and:
        accountMapper.getAccountByUserAndCurrency(USER1, CURRENCY_ID) >> existed

        when:
        def result = accountService.withdraw(USER1, withdrawal)

        then:
        result == expected
        1 * accountMapper.updateAccountValue(expected) >> true

        where:
        withdrawalValue | expectedValue
        100             | 100
        1               | 199
        200             | 0
        150             | 50
        199.99          | 0.01
    }

    def "transfer failed when not enough money"(Account from) {
        given:
        def transfer = new Transfer(USER1, USER2, CURRENCY_ID, 200)

        and:
        accountMapper.getAccountByUserAndCurrency(USER1, CURRENCY_ID) >> from

        when:
        accountService.transfer(transfer)

        then:
        def ex = thrown(AccountException)
        ex.errorMessage.serviceError == ErrorCode.NOT_ENOUGH_MONEY

        where:
        from << [null,
                 new Account(1, USER1, CURRENCY_ID, 100),
                 new Account(1, USER1, CURRENCY_ID, 199.99)]
    }

    def "transfer create to account when to is null"() {
        given:
        def newId = 2
        def transfer = new Transfer(USER1, USER2, CURRENCY_ID, 200)
        def from = new Account(1, USER1, CURRENCY_ID, 300)
        def fromExp = new Account(1, USER1, CURRENCY_ID, 100)
        def toExp = new Account(newId, USER2, CURRENCY_ID, 200)

        and:
        accountMapper.getAccountByUserAndCurrency(USER1, CURRENCY_ID) >> from
        accountMapper.getAccountByUserAndCurrency(USER2, CURRENCY_ID) >> null
        1 * accountMapper.createAccount(new Account(null, USER2, CURRENCY_ID, 0)) >> { Account account -> account.setId(newId); return 1 }

        when:
        accountService.transfer(transfer)

        then:
        1 * accountMapper.updateAccountValue(fromExp) >> true

        1 * accountMapper.updateAccountValue(toExp) >> true
    }

    def "transfer successfull with updated"(BigDecimal transVal, BigDecimal fromVal, BigDecimal toVal,
                                            BigDecimal fromExpVal, BigDecimal toExpVal) {
        given:
        def transfer = new Transfer(USER1, USER2, CURRENCY_ID, transVal)
        def from = new Account(1, USER1, CURRENCY_ID, fromVal)
        def fromExp = new Account(1, USER1, CURRENCY_ID, fromExpVal)
        def to = new Account(2, USER2, CURRENCY_ID, toVal)
        def toExp = new Account(2, USER2, CURRENCY_ID, toExpVal)

        and:
        accountMapper.getAccountByUserAndCurrency(USER1, CURRENCY_ID) >> from
        accountMapper.getAccountByUserAndCurrency(USER2, CURRENCY_ID) >> to

        when:
        accountService.transfer(transfer)

        then:
        1 * accountMapper.updateAccountValue(fromExp) >> true
        1 * accountMapper.updateAccountValue(toExp) >> true

        where:
        transVal | fromVal | toVal | fromExpVal | toExpVal
        100      | 300     | 0     | 200        | 100
        100      | 300     | 300   | 200        | 400
        300      | 300     | 100   | 0          | 400
        199.99   | 200     | 0.01  | 0.01       | 200.00
    }
}
