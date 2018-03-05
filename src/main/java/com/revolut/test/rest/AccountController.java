package com.revolut.test.rest;

import com.revolut.test.dto.Account;
import com.revolut.test.dto.Info;
import com.revolut.test.dto.Transfer;
import com.revolut.test.services.AccountService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {

    private final AccountService accountService;

    @Inject
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GET
    @Path("/users/{userId}")
    public List<Account> getAccountsForUser(@PathParam("userId") Long userId) {
        return accountService.getAccountsForUser(userId);
    }

    @GET
    @Path("/{id}")
    public Account getAccount(@PathParam("id") Long id) {
        return accountService.getAccount(id);
    }

    @POST
    @Path("/users/{userId}/income")
    public Account income(@PathParam("userId") Long userId, @Valid Account account) {
        return accountService.income(userId, account);
    }

    @POST
    @Path("/users/{userId}/withdraw")
    public Account withdraw(@PathParam("userId") Long userId, @Valid Account account) {
        return accountService.withdraw(userId, account);
    }

    @POST
    @Path("/transfers")
    public Info transfer(Transfer transfer) {
        accountService.transfer(transfer);
        return new Info("Transfer successful");
    }
}
