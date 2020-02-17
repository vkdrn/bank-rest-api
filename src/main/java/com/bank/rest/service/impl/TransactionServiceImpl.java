package com.bank.rest.service.impl;

import com.bank.rest.dto.TransactionDTO;
import com.bank.rest.model.TransactionModel;
import com.bank.rest.repository.TransactionRepository;
import com.bank.rest.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository repository;
    private final ModelMapper mapper;

    public TransactionServiceImpl(TransactionRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void performTransaction(TransactionDTO transactionDTO) {
        Assert.notNull(transactionDTO);

        final TransactionModel transaction = mapper.map(transactionDTO, TransactionModel.class);
        repository.create(transaction);
    }

    @Override
    public List<TransactionDTO> getAll() {
        return repository.findAll().stream()
                .map(t -> mapper.map(t, TransactionDTO.class))
                .collect(Collectors.toList());
    }
}
