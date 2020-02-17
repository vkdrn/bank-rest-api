package com.bank.rest.repository.impl;

import com.bank.rest.jooq.bank_schema.tables.records.AccountRecord;
import com.bank.rest.model.TransactionModel;
import com.bank.rest.repository.AccountRepository;
import com.bank.rest.repository.TransactionRepository;
import com.bank.rest.util.validators.TransactionValidator;
import org.jooq.DSLContext;
import org.jooq.tools.JooqLogger;
import spark.utils.Assert;

import java.sql.Timestamp;
import java.util.List;

import static com.bank.rest.jooq.bank_schema.tables.Transaction.TRANSACTION;

public class TransactionRepositoryImpl implements TransactionRepository {

    private static final JooqLogger log = JooqLogger.getLogger(TransactionRepositoryImpl.class);

    private final DSLContext ctx;
    private final AccountRepository accountRepository;
    private final TransactionValidator transactionValidator;

    public TransactionRepositoryImpl(DSLContext ctx, AccountRepository accountRepository, TransactionValidator transactionValidator) {
        this.ctx = ctx;
        this.accountRepository = accountRepository;
        this.transactionValidator = transactionValidator;
    }

    @Override
    public void create(TransactionModel transaction) {
        Assert.notNull(transaction);
        transactionValidator.validateModel(transaction);

        ctx.transaction(configuration -> {
            final AccountRecord source = accountRepository.findRecordById(transaction.getSource());
            transactionValidator.validateSourceRecord(source.getBalance(), transaction.getAmount());
            final AccountRecord target = accountRepository.findRecordById(transaction.getTarget());
            source.setBalance(source.getBalance().subtract(transaction.getAmount()));
            target.setBalance(target.getBalance().add(transaction.getAmount()));

            ctx.insertInto(TRANSACTION)
                    .set(TRANSACTION.SOURCE, transaction.getSource())
                    .set(TRANSACTION.TARGET, transaction.getTarget())
                    .set(TRANSACTION.AMOUNT, transaction.getAmount())
                    .set(TRANSACTION.TRANSACTION_TIME, new Timestamp(System.currentTimeMillis()))
                    .execute();
            source.store();
            target.store();
        });
    }

    @Override
    public List<TransactionModel> findAll() {
        return ctx.selectFrom(TRANSACTION).fetchInto(TransactionModel.class);
    }
}
