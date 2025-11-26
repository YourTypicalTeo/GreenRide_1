-- In: src/main/resources/data.sql

-- Insert ROLE_USER if it doesn't exist
INSERT INTO roles (name)
SELECT 'ROLE_USER'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');

-- Insert ROLE_ADMIN if it doesn't exist
INSERT INTO roles (name)
SELECT 'ROLE_ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');