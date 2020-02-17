package com.bank.rest;

import com.bank.rest.controller.RestController;
import com.bank.rest.repository.AccountRepository;
import com.bank.rest.repository.TransactionRepository;
import com.bank.rest.repository.impl.AccountRepositoryImpl;
import com.bank.rest.repository.impl.TransactionRepositoryImpl;
import com.bank.rest.service.AccountService;
import com.bank.rest.service.TransactionService;
import com.bank.rest.service.impl.AccountServiceImpl;
import com.bank.rest.service.impl.TransactionServiceImpl;
import com.bank.rest.util.validators.TransactionValidator;
import org.apache.commons.dbcp.BasicDataSource;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.ThreadLocalTransactionProvider;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.io.IOException;
import java.util.Properties;

public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        final Properties properties = getProperties();
        final DSLContext jooqDslCtx = getJooqDslCtx(properties);

        AccountRepository accountRepository = new AccountRepositoryImpl(jooqDslCtx);
        TransactionRepository transactionRepository = new TransactionRepositoryImpl(jooqDslCtx, accountRepository, new TransactionValidator());

        final ModelMapper modelMapper = new ModelMapper();

        AccountService accountService = new AccountServiceImpl(accountRepository, modelMapper);
        TransactionService transactionService = new TransactionServiceImpl(transactionRepository, modelMapper);

        RestController controller = new RestController(accountService, transactionService);
        controller.register(Integer.parseInt(properties.getProperty("spark.port")));
    }

    public static void start() {
        main(new String[]{});
        Spark.awaitInitialization();
    }

    public static void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static DSLContext getJooqDslCtx(Properties properties) {
        final BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(properties.getProperty("db.driver"));
        dataSource.setUrl(properties.getProperty("db.url"));
        dataSource.setUsername(properties.getProperty("db.username"));
        dataSource.setPassword(properties.getProperty("db.password"));

        final ConnectionProvider cp = new DataSourceConnectionProvider(dataSource);
        final Configuration configuration = new DefaultConfiguration()
                .set(cp)
                .set(SQLDialect.H2)
                .set(new ThreadLocalTransactionProvider(cp, true))
                .set(new Settings().withExecuteWithOptimisticLocking(true));
        return DSL.using(configuration);
    }

    private static Properties getProperties() {
        final Properties properties = new Properties();
        try {
            properties.load(Application.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            LOG.error("Could not read properties ", e);
            throw new RuntimeException("Could not read properties");
        }
        return properties;
    }
}
