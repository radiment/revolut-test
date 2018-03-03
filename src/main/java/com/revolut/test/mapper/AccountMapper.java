package com.revolut.test.mapper;

import com.revolut.test.dto.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AccountMapper {

    @Select("select * from account a where a.user_id = #{userId} and a.currency_id = #{currencyId}")
    Account getAccountByUserIdAndCurrencyId(@Param("userId") Long userId, @Param("currencyId") int currencyId);

    @Insert("insert into account (user_id, currency_id, value) VALUES (#{userId}, #{currencyId}, #{value})")
    @SelectKey(statement="call identity()", keyProperty="id", before=false, resultType=Long.class)
    Long createAccount(Account newAccount);

    @Select("SELECT * FROM account")
    List<Account> getAllAccounts();

    @Select("SELECT * FROM account where id = #{id}")
    Account getAccountById(@Param("id") Long id);

    @Update("UPDATE account set value = #{value} WHERE id = #{id}")
    void updateAccountValue(Account account);
}
