CREATE TABLE account (id BIGINT NOT NULL, account_limit BIGINT, balance BIGINT NOT NULL, version BIGINT, CONSTRAINT account_pkey PRIMARY KEY (id));
CREATE TABLE account_transaction (uuid UUID NOT NULL, type VARCHAR(1) NOT NULL, description VARCHAR(255) NOT NULL, amount BIGINT NOT NULL, create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, account_id BIGINT NOT NULL, version BIGINT, CONSTRAINT account_transaction_pkey PRIMARY KEY (uuid));
ALTER TABLE account_transaction ADD CONSTRAINT "fk_account_transaction_Account" FOREIGN KEY (account_id) REFERENCES account (id);
CREATE INDEX "idx_transaction-create-at" ON account_transaction(create_at DESC);




