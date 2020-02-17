package com.bank.rest.service.impl;

import com.bank.rest.dto.AccountDTO;
import com.bank.rest.model.AccountModel;
import com.bank.rest.repository.AccountRepository;
import com.bank.rest.service.AccountService;
import com.bank.rest.util.exception.BadRequestFormatException;
import com.bank.rest.util.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.Assert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AccountServiceImpl implements AccountService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository repository;
    private final ModelMapper mapper;

    public AccountServiceImpl(AccountRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<AccountDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO getById(String id) {
        Assert.notNull(id);

        final int accountId = parseId(id);
        return mapToDto(repository.findById(accountId));
    }

    @Override
    public void create(AccountDTO accountDTO) {
        Assert.notNull(accountDTO);

        final AccountModel account = mapper.map(accountDTO, AccountModel.class);
        repository.create(account);
    }

    @Override
    public void update(AccountDTO accountDTO, String id) {
        Assert.notNull(accountDTO);
        Assert.notNull(id);

        final AccountModel account = mapper.map(accountDTO, AccountModel.class);

        final int accountId;
        accountId = parseId(id);

        checkIdsEqual(account, accountId);
        account.setId(accountId);
        repository.update(account);
    }

    @Override
    public void delete(String id) {
        Assert.notNull(id);

        final int accountId = parseId(id);
        repository.delete(accountId);
    }

    private int parseId(String id) {
        Assert.notNull(id);

        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new BadRequestFormatException("Invalid ID parameter. Must be integer");
        }
    }

    private void checkIdsEqual(AccountModel account, int accountId) {
        Assert.notNull(account);

        if (!Objects.equals(account.getId(), accountId)) {
            LOG.warn("Passed not equal params for update. Request ID: {}, Path ID: {}. " +
                    "Ignoring request ID.", account.getId(), accountId);
        }
    }

    private AccountDTO mapToDto(AccountModel model) {
        if (model != null) {
            AccountDTO dto = new AccountDTO();
            dto.setId(model.getId());
            dto.setBalance(model.getBalance());
            dto.setEmail(model.getEmail());

            return dto;
        }

        throw new ResourceNotFoundException("Account ID not found");
    }
}
