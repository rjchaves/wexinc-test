CREATE TABLE purchased_transaction (
    id UUID primary key,
    description VARCHAR(50) NOT NULL,
    transaction_datetime timestamp NOT NULL,
    amount DECIMAL(19, 4) NOT NULL
);