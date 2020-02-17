package com.bank.rest.controller;

import com.bank.rest.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class RestControllerTest {


    private static final String URI_BASE = "http://localhost:9090/api/v1";
    private static final String URI_ACCOUNTS = "/accounts";
    private static final String URI_TRANSACTIONS = "/transactions";
    private static final ObjectMapper mapper = new ObjectMapper();


    @Before
    public void setUp() {
        Application.start();
    }

    @After
    public void tearDown() {
        Application.stop();
    }

    @Test
    public void testGetAllAccount() {
        final List<Map<String, Object>> accounts = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(200)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accounts.size());
        final Map<String, Object> account = accounts.get(2);
        assertEquals(3, account.get("id"));
        assertEquals("matt@matt.com", account.get("email"));
        assertEquals(30.30f, account.get("balance"));
    }

    @Test
    public void testGetAccountById() {
        get(URI_BASE + URI_ACCOUNTS + "/2")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(2))
                .body("balance", equalTo(20.2f))
                .body("email", equalTo("tom@tom.com"));
    }

    @Test
    public void testGetAccountWithWrongId() {
        get(URI_BASE + URI_ACCOUNTS + "/333")
                .then()
                .assertThat()
                .statusCode(HTTP_NOT_FOUND)
                .body(equalTo("Account ID not found"));
    }

    @Test
    public void testGetAccountWithInvalidId() {
        get(URI_BASE + URI_ACCOUNTS + "/abc")
                .then()
                .assertThat()
                .statusCode(HTTP_BAD_REQUEST)
                .body(equalTo("Invalid ID parameter. Must be integer"));
    }

    @Test
    public void testPostNewAccount() {
        final List<Map<String, Object>> accounts = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accounts.size());

        given()
                .body("{\"balance\":555.55,\"email\":\"eric@eric.com\"}")
                .when()
                .post(URI_BASE + URI_ACCOUNTS)
                .then()
                .assertThat()
                .statusCode(HTTP_CREATED);

        final List<Map<String, Object>> accountsAfterInsert = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(4, accountsAfterInsert.size());
        final Map<String, Object> insertedAccount = accountsAfterInsert.get(3);
        assertEquals("eric@eric.com", insertedAccount.get("email"));
        assertEquals(555.55f, insertedAccount.get("balance"));
    }

    @Test
    public void testPostNewAccountInvalidJson() {
        final List<Map<String, Object>> accounts = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accounts.size());

        given()
                .body("{\"balance\":555.55,\"email\":\"eric@eric.com")
                .when()
                .post(URI_BASE + URI_ACCOUNTS)
                .then()
                .assertThat()
                .statusCode(HTTP_BAD_REQUEST);

        final List<Map<String, Object>> accountsAfterInsert = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accountsAfterInsert.size());
    }

    @Test
    public void testPostNewAccountIgnoreIdFromRequest() {
        final List<Map<String, Object>> accounts = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accounts.size());

        given()
                .body("{\"id\":10, \"balance\":555.55,\"email\":\"eric@eric.com\"}")
                .when()
                .post(URI_BASE + URI_ACCOUNTS)
                .then()
                .assertThat()
                .statusCode(HTTP_CREATED);

        final List<Map<String, Object>> accountsAfterInsert = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(4, accountsAfterInsert.size());
        final Map<String, Object> insertedAccount = accountsAfterInsert.get(3);
        assertEquals("eric@eric.com", insertedAccount.get("email"));
        assertEquals(555.55f, insertedAccount.get("balance"));
        assertEquals(4, insertedAccount.get("id"));
    }

    @Test
    public void testPostNewTransaction() {
        final List<Map<String, Object>> accounts = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accounts.size());

        final List<Map<String, Object>> transactionsBefore = get(URI_BASE + URI_TRANSACTIONS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(1, transactionsBefore.size());

        get(URI_BASE + URI_ACCOUNTS + "/1")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(1))
                .body("balance", equalTo(10.1f))
                .body("email", equalTo("john@john.com"));

        get(URI_BASE + URI_ACCOUNTS + "/2")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(2))
                .body("balance", equalTo(20.2f))
                .body("email", equalTo("tom@tom.com"));

        given()
                .body("{\"source\":1, \"target\":2, \"amount\":2.22}")
                .when()
                .post(URI_BASE + URI_TRANSACTIONS)
                .then()
                .assertThat()
                .statusCode(HTTP_CREATED);

        final List<Map<String, Object>> accountsAfterInsert = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accountsAfterInsert.size());

        get(URI_BASE + URI_ACCOUNTS + "/1")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(1))
                .body("balance", equalTo(7.88f))
                .body("email", equalTo("john@john.com"));

        get(URI_BASE + URI_ACCOUNTS + "/2")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(2))
                .body("balance", equalTo(22.42f))
                .body("email", equalTo("tom@tom.com"));

        final List<Map<String, Object>> transactionsAfter = get(URI_BASE + URI_TRANSACTIONS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(2, transactionsAfter.size());
        final Map<String, Object> transaction = transactionsAfter.get(1);
        assertEquals(2, transaction.get("id"));
        assertEquals(1, transaction.get("source"));
        assertEquals(2, transaction.get("target"));
        assertEquals(2.22f, transaction.get("amount"));
    }

    @Test
    public void testPostNewTransactionSameSourceTargetAccount() {
        final List<Map<String, Object>> accounts = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accounts.size());

        final List<Map<String, Object>> transactionsBefore = get(URI_BASE + URI_TRANSACTIONS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(1, transactionsBefore.size());

        get(URI_BASE + URI_ACCOUNTS + "/1")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(1))
                .body("balance", equalTo(10.1f))
                .body("email", equalTo("john@john.com"));

        get(URI_BASE + URI_ACCOUNTS + "/2")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(2))
                .body("balance", equalTo(20.2f))
                .body("email", equalTo("tom@tom.com"));

        given()
                .body("{\"source\":2, \"target\":2, \"amount\":2.22}")
                .when()
                .post(URI_BASE + URI_TRANSACTIONS)
                .then()
                .assertThat()
                .statusCode(HTTP_BAD_REQUEST)
                .body(equalTo("Source account ID cannot be equal to target account ID"));

        final List<Map<String, Object>> accountsAfterInsert = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accountsAfterInsert.size());

        get(URI_BASE + URI_ACCOUNTS + "/2")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(2))
                .body("balance", equalTo(20.2f))
                .body("email", equalTo("tom@tom.com"));

        final List<Map<String, Object>> transactionsAfter = get(URI_BASE + URI_TRANSACTIONS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(1, transactionsAfter.size());
    }

    @Test
    public void testPostNewTransactionNotEnoughMoney() {
        final List<Map<String, Object>> accounts = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accounts.size());

        final List<Map<String, Object>> transactionsBefore = get(URI_BASE + URI_TRANSACTIONS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(1, transactionsBefore.size());

        get(URI_BASE + URI_ACCOUNTS + "/1")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(1))
                .body("balance", equalTo(10.1f))
                .body("email", equalTo("john@john.com"));

        get(URI_BASE + URI_ACCOUNTS + "/2")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(2))
                .body("balance", equalTo(20.2f))
                .body("email", equalTo("tom@tom.com"));

        given()
                .body("{\"source\":1, \"target\":2, \"amount\":1000.11}")
                .when()
                .post(URI_BASE + URI_TRANSACTIONS)
                .then()
                .assertThat()
                .statusCode(HTTP_BAD_REQUEST)
                .body(equalTo("Not enough money on source account."));

        final List<Map<String, Object>> accountsAfterInsert = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accountsAfterInsert.size());

        get(URI_BASE + URI_ACCOUNTS + "/1")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(1))
                .body("balance", equalTo(10.1f))
                .body("email", equalTo("john@john.com"));

        get(URI_BASE + URI_ACCOUNTS + "/2")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(2))
                .body("balance", equalTo(20.2f))
                .body("email", equalTo("tom@tom.com"));

        final List<Map<String, Object>> transactionsAfter = get(URI_BASE + URI_TRANSACTIONS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(1, transactionsAfter.size());
    }

    @Test
    public void testPostNewTransactionInvalidJson() {
        final List<Map<String, Object>> accounts = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accounts.size());

        final List<Map<String, Object>> transactionsBefore = get(URI_BASE + URI_TRANSACTIONS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(1, transactionsBefore.size());

        get(URI_BASE + URI_ACCOUNTS + "/1")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(1))
                .body("balance", equalTo(10.1f))
                .body("email", equalTo("john@john.com"));

        get(URI_BASE + URI_ACCOUNTS + "/2")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(2))
                .body("balance", equalTo(20.2f))
                .body("email", equalTo("tom@tom.com"));

        given()
                .body("{\"source\":1, \"target\":2, \"amount\":1000.11")
                .when()
                .post(URI_BASE + URI_TRANSACTIONS)
                .then()
                .assertThat()
                .statusCode(HTTP_BAD_REQUEST);

        final List<Map<String, Object>> accountsAfterInsert = get(URI_BASE + URI_ACCOUNTS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(3, accountsAfterInsert.size());

        get(URI_BASE + URI_ACCOUNTS + "/1")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(1))
                .body("balance", equalTo(10.1f))
                .body("email", equalTo("john@john.com"));

        get(URI_BASE + URI_ACCOUNTS + "/2")
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("id", equalTo(2))
                .body("balance", equalTo(20.2f))
                .body("email", equalTo("tom@tom.com"));

        final List<Map<String, Object>> transactionsAfter = get(URI_BASE + URI_TRANSACTIONS).then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .jsonPath().getList("$");
        assertEquals(1, transactionsAfter.size());
    }
}
