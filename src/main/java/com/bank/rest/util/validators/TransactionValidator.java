package com.bank.rest.util.validators;

import com.bank.rest.model.TransactionModel;
import com.bank.rest.util.exception.BadRequestFormatException;
import com.bank.rest.util.exception.ValidationNotEnouhMoneyException;

import java.math.BigDecimal;

public class TransactionValidator {
    public void validateSourceRecord(BigDecimal balance, BigDecimal amount) {
        if (balance == null || amount == null || balance.compareTo(amount) < 0) {
            throw new ValidationNotEnouhMoneyException("Not enough money on source account.");
        }
    }

    public void validateModel(TransactionModel transaction) {
        if (transaction.getSource() == null || transaction.getTarget() == null || transaction.getAmount() == null) {
            throw new BadRequestFormatException("Bad request");
        }

        if (transaction.getSource().equals(transaction.getTarget())) {
            throw new BadRequestFormatException("Source account ID cannot be equal to target account ID");
        }
    }
}
