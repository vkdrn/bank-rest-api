package com.bank.rest.service;

import com.bank.rest.dto.TransactionDTO;

import java.util.List;

public interface TransactionService {
    /**
     * Transfers provided amount from source account to target.
     * After completion creates new entry in Transaction table
     *
     * @param transactionDTO transaction with source, target ids, and amount
     */
    void performTransaction(TransactionDTO transactionDTO);

    /**
     * Finds all transactions
     *
     * @return list of transactions
     */
    List<TransactionDTO> getAll();
}
