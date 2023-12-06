CREATE TABLE Client (
    id VARCHAR(40) PRIMARY KEY,
    first_name VARCHAR(40),
    last_name VARCHAR(40),
    email VARCHAR(40) UNIQUE,
    salary DECIMAL(10, 2)
);

CREATE TABLE Application (
    id VARCHAR(40) PRIMARY KEY,
    client_id VARCHAR(40) REFERENCES Client(id),
    requested_disbursement_amount DECIMAL (10, 2),
    status VARCHAR(40),
    CONSTRAINT status_check CHECK (status IN ('NEW', 'SCORING', 'ACCEPTED', 'ACTIVE', 'CLOSED'))
);