package com.revolut.test.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.revolut.test.mapper.AccountMapper;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.mybatis.guice.MyBatisModule;
import pl.coffeepower.guiceliquibase.GuiceLiquibaseConfig;
import pl.coffeepower.guiceliquibase.GuiceLiquibaseModule;
import pl.coffeepower.guiceliquibase.LiquibaseConfig;
import pl.coffeepower.guiceliquibase.annotation.GuiceLiquibaseConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

public class DIConfig extends AbstractModule {
    @Override
    protected void configure() {
        install(new ServletModule());
        install(new GuiceLiquibaseModule());
        install(new MyBatisModule() {
            @Override
            protected void initialize() {
                bindTransactionFactoryType(JdbcTransactionFactory.class);
                addMapperClass(AccountMapper.class);

                Names.bindProperties(binder(), getApplicationProperties());
            }
        });
    }

    private Properties getApplicationProperties() {
        Properties properties = new Properties();
        try {
            properties.load(DIConfig.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Error loading application properties", e);
        }
        return properties;
    }

    @Singleton
    @Provides public DataSource createDataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:~/account");
        ds.setUser("user");
        ds.setPassword("123");
        return ds;
    }

    @GuiceLiquibaseConfiguration
    @Provides
    @Inject
    private GuiceLiquibaseConfig createLiquibaseConfig(DataSource dataSource) {
        return GuiceLiquibaseConfig.Builder
                .of(LiquibaseConfig.Builder.of(dataSource)
                        .withChangeLogPath("db/changelog-master.xml")
                        .build())
                .build();
    }

}
