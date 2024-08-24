CREATE TABLE fee
(
    id            BIGSERIAL PRIMARY KEY,
    currency_from VARCHAR(10)   NOT NULL,
    currency_to   VARCHAR(10)   NOT NULL,
    value         NUMERIC(6, 5) NOT NULL
);