package com.revolut.test.mapper;

import com.revolut.test.dto.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AccountMapper {

    @Select("select * from account a where a.user_id = #{userId} and a.currency_id = #{currencyId}")
    Account getAccountByUserAndCurrency(@Param("userId") Long userId, @Param("currencyId") int currencyId);

    @Insert("insert into account (user_id, currency_id, value, version) VALUES (#{userId}, #{currencyId}, #{value}, #{version})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    int createAccount(Account newAccount);

    @Select("SELECT * FROM account")
    List<Account> getAllAccounts();

    @Select("SELECT * FROM account where id = #{id}")
    Account getAccountById(@Param("id") Long id);

    @Update("UPDATE account set value = #{value}, version = version + 1 WHERE id = #{id} and version = #{version}")
    boolean updateAccountValue(Account account);

    @Select("select * from account a where a.user_id = #{userId}")
    List<Account> getAccountsByUser(@Param("userId") Long userId);
}
