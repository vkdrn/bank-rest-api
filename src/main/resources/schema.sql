CREATE SCHEMA IF NOT EXISTS BANK_SCHEMA;
SET SCHEMA BANK_SCHEMA;
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS account;

CREATE TABLE account
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    email   VARCHAR(100)         NOT NULL,
    balance DECIMAL              NOT NULL,
    active  BOOLEAN DEFAULT TRUE NOT NULL,

    CONSTRAINT uq_email UNIQUE (email)
);

CREATE TABLE transaction
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    source           INT       NOT NULL,
    target           INT       NOT NULL,
    amount           DECIMAL   NOT NULL,
    transaction_time TIMESTAMP NOT NULL,

    CONSTRAINT fk_ac_source FOREIGN KEY (source) REFERENCES account (id),
    CONSTRAINT fk_ac_target FOREIGN KEY (target) REFERENCES account (id)
);

INSERT INTO account
VALUES (1, 'john@john.com', 10.10, true),
       (2, 'tom@tom.com', 20.20, true),
       (3, 'matt@matt.com', 30.30, true);

INSERT INTO transaction
VALUES (1, 1, 3, 10, CURRENT_TIMESTAMP());