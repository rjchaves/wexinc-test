CREATE TABLE `transaction` (
    id UUID primary key,
    description VARCHAR(50) NOT NULL,
    transaction_date DATE NOT NULL,
    amount DECIMAL(19, 2) NOT NULL
);