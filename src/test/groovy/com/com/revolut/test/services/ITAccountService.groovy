package com.com.revolut.test.services

import com.revolut.test.server.JettyServer
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static com.revolut.test.services.exception.ErrorCode.NOT_ENOUGH_MONEY
import static groovyx.net.http.ContentType.JSON
import static org.hamcrest.Matchers.*
import static spock.util.matcher.HamcrestSupport.expect

@Stepwise
class ITAccountService extends Specification {

    public static final int PORT = 16666
    private static final int USER1 = 100
    private static final int USER2 = 200
    private static final int CURRENCY = 810


    @Shared
    def client
    @Shared
    def accountId

    def setupSpec() {
        new JettyServer().start(PORT);
        client = new RESTClient("http://localhost:$PORT", JSON)
        client.handler.failure = client.handler.success
    }

    def "create income to user 1" () {
        given: "100 rub for income"
        def income = [value:100, currencyId: CURRENCY]

        when: "request income for user1"
        def resp = client.post(path: "/accounts/users/${USER1}/income", body: income)

        then:
        resp.status == 200
        resp.data.userId == USER1
        resp.data.value == 100
        resp.data.currencyId == CURRENCY
        (accountId = resp.data.id) != null

    }

    def "get all accounts"() {
        when:
        def resp = client.get(path: '/accounts')

        then:
        resp.status == 200
        expect(resp.data.id, contains(accountId))
        expect(resp.data.userId, contains(USER1))
    }

    def "get accounts for non existent user returns empty list"() {
        when:
        def resp = client.get(path: "/accounts/users/$USER2")

        then:
        resp.status == 200
        expect(resp.data, empty())
    }

    def "get accounts for existent user returns account data"() {
        when:
        def resp = client.get(path: "/accounts/users/$USER1")

        then:
        resp.status == 200
        expect(resp.data, not(empty()))
        expect(resp.data.currencyId, contains(CURRENCY))
        expect(resp.data.userId, contains(USER1))
    }

    def "income increase existed value account"() {
        given: "350 rub for income"
        def income = [value:350, currencyId: CURRENCY]

        when: "request income for user1"
        def resp = client.post(path: "/accounts/users/${USER1}/income", body: income)

        then:
        resp.status == 200
        resp.data.value == 450
        resp.data.currencyId == CURRENCY
        resp.data.id == accountId
    }

    def "transfer money from non existent user will fail"() {
        given:
        def transfer = [userFrom: USER2, userTo: USER1, value: 100, currencyId: CURRENCY]

        when:
        def resp = client.post(path: '/accounts/transfers', body: transfer)
        def from = client.get(path: "/accounts/users/$USER2")
        def to = client.get(path: "/accounts/users/$USER1")

        then:
        resp.status == 400
        resp.data.code == NOT_ENOUGH_MONEY.getCode()
        expect(to.data.value, contains(450))
        expect(from.data, empty())
    }

    def "transfer money if it's enough to any person is ok"() {
        given:
        def transfer = [userFrom: USER1, userTo: USER2, value: 100, currencyId: CURRENCY]

        when:
        def resp = client.post(path: '/accounts/transfers', body: transfer)
        def from = client.get(path: "/accounts/users/$USER1")
        def to = client.get(path: "/accounts/users/$USER2")

        then:
        resp.status == 200
        resp.data.message == 'Transfer successful'
        expect(from.data.value, contains(350))
        expect(to.data.value, contains(100))
    }

    def "transfer money when it's not enough failed"() {
        given:
        def transfer = [userFrom: USER1, userTo: USER2, value: 400, currencyId: CURRENCY]

        when:
        def resp = client.post(path: '/accounts/transfers', body: transfer)
        def from = client.get(path: "/accounts/users/$USER1")
        def to = client.get(path: "/accounts/users/$USER2")

        then:
        resp.status == 400
        resp.data.code == NOT_ENOUGH_MONEY.getCode()
        expect(from.data.value, contains(350))
        expect(to.data.value, contains(100))
    }
}
