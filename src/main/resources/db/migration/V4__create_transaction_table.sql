CREATE TABLE transaction (
    transaction_id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    currency currency_enum NOT NULL,
    direction transaction_direction_enum NOT NULL,
    description TEXT NOT NULL,
    balance_after_transaction DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES account(account_id)
);

CREATE INDEX idx_transaction_account_id ON transaction(account_id);