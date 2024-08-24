CREATE UNIQUE INDEX idx_fee_currencyfrom_currencyto
    ON fee (currency_from, currency_to);
