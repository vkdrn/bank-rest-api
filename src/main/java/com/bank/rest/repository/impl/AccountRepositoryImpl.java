package com.bank.rest.repository.impl;

import com.bank.rest.jooq.bank_schema.tables.records.AccountRecord;
import com.bank.rest.model.AccountModel;
import com.bank.rest.repository.AccountRepository;
import com.bank.rest.util.exception.ResourceNotFoundException;
import org.jooq.DSLContext;
import spark.utils.Assert;

import java.util.List;

import static com.bank.rest.jooq.bank_schema.tables.Account.ACCOUNT;

public class AccountRepositoryImpl implements AccountRepository {

    private final DSLContext ctx;

    public AccountRepositoryImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public List<AccountModel> findAll() {
        return ctx.selectFrom(ACCOUNT)
                .where(ACCOUNT.ACTIVE.eq(true))
                .fetchInto(AccountModel.class);
    }

    @Override
    public void create(AccountModel account) {
        Assert.notNull(account);

        ctx.insertInto(ACCOUNT)
                .set(ACCOUNT.EMAIL, account.getEmail())
                .set(ACCOUNT.BALANCE, account.getBalance())
                .set(ACCOUNT.ACTIVE, true)
                .execute();
    }

    @Override
    public AccountModel findById(int id) {
        return ctx.selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(id))
                .and(ACCOUNT.ACTIVE.eq(true))
                .fetchAnyInto(AccountModel.class);
    }

    @Override
    public AccountRecord findRecordById(int id) {
        return ctx.selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(id))
                .and(ACCOUNT.ACTIVE.eq(true))
                .fetchAny();
    }

    @Override
    public void update(AccountModel account) {
        Assert.notNull(account);

        final int updatedRows = ctx.update(ACCOUNT)
                .set(ACCOUNT.EMAIL, account.getEmail())
                .set(ACCOUNT.BALANCE, account.getBalance())
                .where(ACCOUNT.ID.eq(account.getId()))
                .and(ACCOUNT.ACTIVE.eq(true))
                .execute();

        checkRowsUpdated(updatedRows);
    }

    @Override
    public void delete(int accountId) {
        final int updatedRows = ctx.update(ACCOUNT)
                .set(ACCOUNT.ACTIVE, false)
                .where(ACCOUNT.ID.eq(accountId))
                .and(ACCOUNT.ACTIVE.eq(true))
                .execute();
        checkRowsUpdated(updatedRows);
    }

    private void checkRowsUpdated(int updatedRows) {
        if (updatedRows == 0) {
            throw new ResourceNotFoundException("Account ID not found");
        }
    }
}
