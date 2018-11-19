INSERT INTO user (id, username, password) VALUES (1, 'manager', 'manager');
INSERT INTO user (id, username, password) VALUES (2, 'employee', 'employee');
INSERT INTO user (id, username, password) VALUES (3, 'user', 'user');
INSERT INTO role (id, name) VALUES (4, 'ROLE_MANAGER');
INSERT INTO role (id, name) VALUES (5, 'ROLE_EMPLOYEE');
INSERT INTO role (id, name) VALUES (6, 'ROLE_USER');
INSERT INTO user_roles (user_id, role_id) VALUES (1, 4);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 5);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 6);
