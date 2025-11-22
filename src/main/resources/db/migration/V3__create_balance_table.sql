CREATE TABLE balance (
    balance_id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    available_amount DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    currency currency_enum NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES account(account_id),
    UNIQUE (account_id, currency)
);

CREATE INDEX idx_balance_account_id ON balance(account_id);