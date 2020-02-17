package com.bank.rest.controller;

import com.bank.rest.dto.AccountDTO;
import com.bank.rest.dto.TransactionDTO;
import com.bank.rest.service.AccountService;
import com.bank.rest.service.TransactionService;
import com.bank.rest.util.exception.BadRequestFormatException;
import com.bank.rest.util.exception.ResourceNotFoundException;
import com.bank.rest.util.exception.ValidationNotEnouhMoneyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.exception.DataAccessException;
import spark.Response;

import static java.net.HttpURLConnection.*;
import static spark.Spark.*;

public class RestController {

    private static final String EMPTY_BODY = "";

    private final AccountService accountService;
    private final TransactionService transactionService;

    public RestController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    public void register(int serverPort) {
        port(serverPort);

        path("/api/v1",
                () -> {
                    path("/accounts",
                            () -> {
                                get("", (req, res) -> addTypeAndStatus(accountService.getAll(), res, HTTP_OK), this::transform);

                                get("/:id", (req, res) -> addTypeAndStatus(accountService.getById(req.params("id")), res, HTTP_OK), this::transform);

                                post("", (req, res) -> {
                                    accountService.create(transformAccountToDTO(req.body()));
                                    res.status(HTTP_CREATED);
                                    return EMPTY_BODY;
                                });

                                put("/:id", (req, res) -> {
                                    accountService.update(transformAccountToDTO(req.body()), req.params("id"));
                                    res.status(HTTP_OK);
                                    return EMPTY_BODY;
                                });

                                delete("/:id", (req, res) -> {
                                    accountService.delete(req.params("id"));
                                    res.status(HTTP_OK);
                                    return EMPTY_BODY;
                                });
                            });
                    path("/transactions",
                            () -> {
                                post("", (req, res) -> {
                                    transactionService.performTransaction(transformTransactionToDTO(req.body()));
                                    res.status(HTTP_CREATED);
                                    return EMPTY_BODY;
                                });

                                get("", (req, res) -> addTypeAndStatus(transactionService.getAll(), res, HTTP_OK), this::transform);
                            });
                });

        exception(JsonProcessingException.class, (e, request, response) -> {
            response.type("application/json");
            response.status(HTTP_BAD_REQUEST);
            response.body(e.getOriginalMessage());
        });

        exception(ResourceNotFoundException.class, (e, request, response) -> {
            response.type("application/json");
            response.status(HTTP_NOT_FOUND);
            response.body(e.getMessage());
        });

        exception(BadRequestFormatException.class, (e, request, response) -> {
            response.type("application/json");
            response.status(HTTP_BAD_REQUEST);
            response.body(e.getMessage());
        });

        exception(ValidationNotEnouhMoneyException.class, (e, request, response) -> {
            response.type("application/json");
            response.status(HTTP_BAD_REQUEST);
            response.body(e.getMessage());
        });

        exception(DataAccessException.class, (e, request, response) -> {
            response.type("application/json");
            response.status(HTTP_BAD_REQUEST);
        });

        exception(IllegalArgumentException.class, (e, request, response) -> {
            response.type("application/json");
            response.status(HTTP_BAD_REQUEST);
        });
    }

    private String transform(Object source) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(source);
    }

    private AccountDTO transformAccountToDTO(String source) throws JsonProcessingException {
        return new ObjectMapper().readValue(source, AccountDTO.class);
    }

    private TransactionDTO transformTransactionToDTO(String source) throws JsonProcessingException {
        return new ObjectMapper().readValue(source, TransactionDTO.class);
    }

    private Object addTypeAndStatus(Object body, Response res, int statusCode) {
        res.status(statusCode);
        res.type("application/json");
        return body;
    }

}
