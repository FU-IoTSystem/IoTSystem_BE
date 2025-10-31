-- Drop and recreate wallet_transactions table to fix constraint issues
-- Run this script in MySQL if you encounter constraint violation errors

USE iot_rental;

-- Drop the table if exists
DROP TABLE IF EXISTS wallet_transactions;

-- Recreate the table without constraint
CREATE TABLE wallet_transactions (
    id BINARY(16) NOT NULL PRIMARY KEY,
    amount DOUBLE,
    transaction_type VARCHAR(50),
    description VARCHAR(255),
    payment_method VARCHAR(100),
    transaction_status VARCHAR(50),
    wallet_id BINARY(16),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (wallet_id) REFERENCES wallet(id),
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_status (transaction_status),
    INDEX idx_type (transaction_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

