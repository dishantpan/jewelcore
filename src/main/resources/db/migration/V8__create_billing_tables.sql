-- V8__create_billing_tables.sql
-- Billing module tables: Customer, Sale, SaleItem, Payment, Invoice

-- Customer table
CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    gst_number VARCHAR(15),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_customer_phone UNIQUE (phone),
    CONSTRAINT uk_customer_email UNIQUE (email)
);

-- Sale table
CREATE TABLE sale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_number VARCHAR(50) NOT NULL,
    customer_id BIGINT NOT NULL,
    sale_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(19,4) NOT NULL,
    discount_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    taxable_amount DECIMAL(19,4) NOT NULL,
    gst_rate DECIMAL(5,2) NOT NULL DEFAULT 3.00,
    gst_amount DECIMAL(19,4) NOT NULL,
    grand_total DECIMAL(19,4) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_sale_number UNIQUE (sale_number),
    CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_sale_created_by FOREIGN KEY (created_by) REFERENCES user_account(id)
);

-- SaleItem table
CREATE TABLE sale_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    inventory_item_id BIGINT NOT NULL,
    jewellery_id BIGINT NOT NULL,
    metal_name VARCHAR(100) NOT NULL,
    purity_name VARCHAR(50) NOT NULL,
    purity_percentage DECIMAL(5,2) NOT NULL,
    gross_weight DECIMAL(19,4) NOT NULL,
    stone_weight DECIMAL(19,4) NOT NULL DEFAULT 0,
    net_weight DECIMAL(19,4) NOT NULL,
    metal_rate_per_gram DECIMAL(19,4) NOT NULL,
    pure_metal_rate_per_gram DECIMAL(19,4) NOT NULL,
    metal_value DECIMAL(19,4) NOT NULL,
    making_charge DECIMAL(19,4) NOT NULL,
    item_price DECIMAL(19,4) NOT NULL,
    gst_rate DECIMAL(5,2) NOT NULL DEFAULT 3.00,
    gst_amount DECIMAL(19,4) NOT NULL,
    total_amount DECIMAL(19,4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sale_item_sale FOREIGN KEY (sale_id) REFERENCES sale(id) ON DELETE CASCADE,
    CONSTRAINT fk_sale_item_inventory FOREIGN KEY (inventory_item_id) REFERENCES inventory_item(id),
    CONSTRAINT fk_sale_item_jewellery FOREIGN KEY (jewellery_id) REFERENCES jewellery(id)
);

-- Payment table
CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reference_number VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_sale FOREIGN KEY (sale_id) REFERENCES sale(id) ON DELETE CASCADE
);

-- Invoice table
CREATE TABLE invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL,
    sale_id BIGINT NOT NULL UNIQUE,
    invoice_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    business_name VARCHAR(255) NOT NULL,
    business_address TEXT NOT NULL,
    business_gst_number VARCHAR(15),
    customer_name VARCHAR(255) NOT NULL,
    customer_address TEXT,
    customer_gst_number VARCHAR(15),
    subtotal DECIMAL(19,4) NOT NULL,
    discount_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    taxable_amount DECIMAL(19,4) NOT NULL,
    gst_rate DECIMAL(5,2) NOT NULL,
    gst_amount DECIMAL(19,4) NOT NULL,
    grand_total DECIMAL(19,4) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20),
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_invoice_number UNIQUE (invoice_number),
    CONSTRAINT fk_invoice_sale FOREIGN KEY (sale_id) REFERENCES sale(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_sale_customer ON sale(customer_id);
CREATE INDEX idx_sale_date ON sale(sale_date);
CREATE INDEX idx_sale_created_by ON sale(created_by);
CREATE INDEX idx_sale_item_sale ON sale_item(sale_id);
CREATE INDEX idx_sale_item_inventory ON sale_item(inventory_item_id);
CREATE INDEX idx_payment_sale ON payment(sale_id);
CREATE INDEX idx_invoice_sale ON invoice(sale_id);
CREATE INDEX idx_invoice_number ON invoice(invoice_number);