CREATE TABLE Product (
    code VARCHAR(10) PRIMARY KEY,
    min_term INT,
    max_term INT,
    min_principal_amount DECIMAL(10, 2),
    max_principal_amount DECIMAL(10, 2),
    min_interest DECIMAL (5, 4),
    max_interest DECIMAL (5, 4),
    min_origination_amount DECIMAL(10, 2),
    max_origination_amount DECIMAL(10, 2)
);

CREATE TABLE Agreement (
    id SERIAL PRIMARY KEY,
    product_code VARCHAR(10) REFERENCES Product(code),
    client_id INT,
    interest DECIMAL (5, 4),
    term INT,
    principal_amount DECIMAL(10, 2),
    origination_amount DECIMAL(10, 2),
    status VARCHAR(40),
    disbursement_date DATE,
    next_payment_date DATE,
    CONSTRAINT agreement_status_check CHECK (status IN ('NEW', 'ACTIVE', 'CLOSED'))
);

CREATE TABLE Payment_Schedule (
    id SERIAL PRIMARY KEY,
    agreement_number INT REFERENCES Agreement(id),
    version INT
);

CREATE TABLE Payment_Schedule_Unit (
    payment_schedule_id INT REFERENCES Payment_Schedule(id),
    status VARCHAR(40),
    payment_date DATE,
    period_payment DECIMAL(10, 2),
    interest_payment DECIMAL(10, 2),
    principal_payment DECIMAL(10, 2),
    period_number INT,
    CONSTRAINT payment_unit_status_check CHECK (status IN ('PAID', 'OVERDUE', 'FUTURE')),
    PRIMARY KEY (payment_schedule_id, period_number)
);
