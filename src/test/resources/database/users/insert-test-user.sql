INSERT INTO users (id, email, first_name, last_name, password, is_deleted)
VALUES (3, 'test@gmail.com', 'Bob', 'Tester', 'password123', false);

INSERT INTO users_roles (user_id, role_id) VALUES (3, 2);