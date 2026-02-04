-- Migration script for JOINED inheritance schema
-- This creates proper FK relationships between base and subclass tables
-- Run after first application startup creates the new tables

USE portfolio_db;

-- With JOINED inheritance, Hibernate creates:
-- 1. 'assets' table with common fields (id, symbol, name, quantity, buy_price, etc.)
-- 2. Subclass tables (stocks, bonds, etc.) with ONLY type-specific fields + FK to assets.id

-- The schema now looks like:
-- assets (base table)
--   ├── id (PK)
--   ├── asset_type (discriminator)
--   ├── symbol, name, quantity, buy_price, purchase_date, created_at, updated_at
--   │
--   ├── stocks.id (FK → assets.id) + exchange, sector, dividend_yield, market_cap
--   ├── bonds.id (FK → assets.id) + coupon_rate, maturity_date, issuer, bond_type, credit_rating
--   ├── etfs.id (FK → assets.id) + expense_ratio, category, holdings_count, dividend_yield
--   ├── mutual_funds.id (FK → assets.id) + fund_family, expense_ratio, category
--   ├── cryptos.id (FK → assets.id) + blockchain, wallet_address, staking_enabled, staking_apy
--   ├── real_estates.id (FK → assets.id) + property_address, property_type, rental_income
--   └── cash_holdings.id (FK → assets.id) + currency, account_type, interest_rate, bank_name

-- Benefits of JOINED inheritance:
-- 1. No field duplication (common fields only in 'assets' table)
-- 2. Polymorphic queries work: SELECT * FROM assets
-- 3. Proper FK relationships between parent and child tables
-- 4. Easy analytics across all asset types

-- Sample insert for a stock (requires insert in both tables):
-- INSERT INTO assets (symbol, name, quantity, buy_price, asset_type, purchase_date)
-- VALUES ('AAPL', 'Apple Inc.', 50, 150.00, 'STOCK', '2024-01-15');
-- 
-- INSERT INTO stocks (id, exchange, sector, market_cap)
-- VALUES (LAST_INSERT_ID(), 'NASDAQ', 'Technology', 'Large');
