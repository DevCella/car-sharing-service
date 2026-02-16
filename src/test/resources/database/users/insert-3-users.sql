
INSERT INTO roles (id, name) VALUES (2, 'CUSTOMER') ON DUPLICATE KEY UPDATE id=id;

INSERT INTO users (id, email, first_name, last_name, password)
VALUES (3, 'test@gmail.com', 'test', 'test', 'password');

INSERT INTO users_roles (user_id, role_id) VALUES (3, 2);