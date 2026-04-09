-- ──────────────────────────────────────────────────────────────────────────
-- N-Layered Architecture Enhanced — PostgreSQL Schema
-- ──────────────────────────────────────────────────────────────────────────

CREATE TABLE departments (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE roles (
    id          BIGSERIAL   PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE employees (
    id            BIGSERIAL      PRIMARY KEY,
    first_name    VARCHAR(100)   NOT NULL,
    last_name     VARCHAR(100)   NOT NULL,
    email         VARCHAR(150)   NOT NULL UNIQUE,
    phone         VARCHAR(20),
    department_id BIGINT         REFERENCES departments (id),
    role_id       BIGINT         REFERENCES roles (id),
    salary        NUMERIC(12, 2) NOT NULL,
    hire_date     DATE           NOT NULL,
    status        VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Self-referencing FK added after employee table exists
ALTER TABLE departments
    ADD COLUMN manager_employee_id BIGINT REFERENCES employees (id);

CREATE TABLE customers (
    id         BIGSERIAL    PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    phone      VARCHAR(20),
    address    TEXT,
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE orders (
    id                       BIGSERIAL      PRIMARY KEY,
    customer_id              BIGINT         NOT NULL REFERENCES customers (id),
    processed_by_employee_id BIGINT         REFERENCES employees (id),
    status                   VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    total_amount             NUMERIC(12, 2) NOT NULL DEFAULT 0,
    notes                    TEXT,
    created_at               TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items (
    id           BIGSERIAL      PRIMARY KEY,
    order_id     BIGINT         NOT NULL REFERENCES orders (id),
    product_id   BIGINT         NOT NULL,
    product_name VARCHAR(200)   NOT NULL,
    quantity     INT            NOT NULL CHECK (quantity > 0),
    unit_price   NUMERIC(12, 2) NOT NULL,
    total_price  NUMERIC(12, 2) NOT NULL,
    created_at   TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- ── Indexes ──────────────────────────────────────────────────────────────
CREATE INDEX idx_employees_department ON employees (department_id);
CREATE INDEX idx_employees_role       ON employees (role_id);
CREATE INDEX idx_employees_status     ON employees (status);
CREATE INDEX idx_customers_status     ON customers (status);
CREATE INDEX idx_customers_email      ON customers (email);
CREATE INDEX idx_orders_customer      ON orders (customer_id);
CREATE INDEX idx_orders_status        ON orders (status);
CREATE INDEX idx_order_items_order    ON order_items (order_id);