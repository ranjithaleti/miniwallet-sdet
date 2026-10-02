CREATE TABLE accounts (
    id          UUID PRIMARY KEY,
    owner_name  VARCHAR(100)  NOT NULL,
    email       VARCHAR(255)  NOT NULL UNIQUE,
    currency    VARCHAR(3)    NOT NULL DEFAULT 'USD',
    type        VARCHAR(10)   NOT NULL CHECK (type IN ('USER', 'SYSTEM')),
    balance     NUMERIC(19,4) NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE TABLE transactions (
    id          UUID PRIMARY KEY,
    type        VARCHAR(10)   NOT NULL CHECK (type IN ('TOPUP', 'TRANSFER', 'REFUND')),
    status      VARCHAR(10)   NOT NULL CHECK (status IN ('COMPLETED', 'FAILED')),
    amount      NUMERIC(19,4) NOT NULL CHECK (amount > 0),
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE TABLE ledger_entries (
    id             UUID PRIMARY KEY,
    transaction_id UUID          NOT NULL REFERENCES transactions(id),
    account_id     UUID          NOT NULL REFERENCES accounts(id),
    direction      VARCHAR(6)    NOT NULL CHECK (direction IN ('DEBIT', 'CREDIT')),
    amount         NUMERIC(19,4) NOT NULL CHECK (amount > 0),
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_ledger_account     ON ledger_entries(account_id, created_at DESC);
CREATE INDEX idx_ledger_transaction ON ledger_entries(transaction_id);

-- System clearing account (the other side of every top-up)
INSERT INTO accounts (id, owner_name, email, type)
VALUES ('00000000-0000-0000-0000-000000000001', 'System Clearing', 'system@miniwallet.internal', 'SYSTEM');