package com.bank.rest.service;

import com.bank.rest.dto.AccountDTO;

import java.util.List;

public interface AccountService {
    /**
     * Gets all accounts
     *
     * @return list of accounts
     */
    List<AccountDTO> getAll();

    /**
     * Gets account by id or throws ResourceNotFoundException if account cannot be found
     *
     * @param id account id
     * @return AccountModel or throws ResourceNotFoundException, if there's no account with provided id
     */
    AccountDTO getById(String id);

    /**
     * Creates new account
     *
     * @param accountDTO new account DTO
     */
    void create(AccountDTO accountDTO);

    /**
     * Updates account with provided id with dto fields
     *
     * @param accountDTO dto to update account
     * @param id         account id to update
     */
    void update(AccountDTO accountDTO, String id);

    /**
     * Deletes (marks inactive) account by id
     *
     * @param id account id to be removed
     */
    void delete(String id);
}
