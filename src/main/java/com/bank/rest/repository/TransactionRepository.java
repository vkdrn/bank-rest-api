package com.bank.rest.repository;

import com.bank.rest.model.TransactionModel;

import java.util.List;

public interface TransactionRepository {
    /**
     * Transfers provided amount from source account to target.
     * After completion creates new entry in Transaction table
     *
     * @param transaction transaction with source, target ids, and mount
     */
    void create(TransactionModel transaction);

    /**
     * Finds all transactions
     *
     * @return list of transactions
     */
    List<TransactionModel> findAll();
}
