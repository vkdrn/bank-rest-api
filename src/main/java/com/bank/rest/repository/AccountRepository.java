package com.bank.rest.repository;

import com.bank.rest.jooq.bank_schema.tables.records.AccountRecord;
import com.bank.rest.model.AccountModel;

import java.util.List;

public interface AccountRepository {
    /**
     * Finds all accounts
     *
     * @return list of accounts
     */
    List<AccountModel> findAll();

    /**
     * Creates new account
     *
     * @param account new account model
     */
    void create(AccountModel account);

    /**
     * Finds account by id or null if account cannot be found
     *
     * @param id account id
     * @return AccountModel or null, if there's no account with provided id
     */
    AccountModel findById(int id);

    /**
     * Finds account record by id or null if account record cannot be found
     *
     * @param id account id
     * @return AccountRecord or null, if there's no account with provided id
     */
    AccountRecord findRecordById(int id);

    /**
     * Updates account with fields of provided model
     *
     * @param account model that would be stored by model's id
     */
    void update(AccountModel account);

    /**
     * Deletes (marks inactive) account by id
     *
     * @param accountId account id to be removed
     */
    void delete(int accountId);
}
