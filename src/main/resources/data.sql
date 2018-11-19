INSERT INTO user (id, username, password) VALUES (99, 'manager', '$2a$10$5ogPZlC58nrVnulKFR5WIOSP2JXW3OlDtMhQ7wqrbwOr9E4cgw.Ou');
INSERT INTO user (id, username, password) VALUES (98, 'employee', '$2a$10$l8rndXHlJrAbwD2eZdw8I.ljhQTHuyZxFt9QBTbAtBWUDhRkMnYE6');
INSERT INTO user (id, username, password) VALUES (97, 'user', '$2a$10$MjX6RnD4IwmuHgkeZaFtN.pIK5jVQIe8B2s72ywL51lfpoAezz7oa');
INSERT INTO role (id, name) VALUES (96, 'ROLE_MANAGER');
INSERT INTO role (id, name) VALUES (95, 'ROLE_EMPLOYEE');
INSERT INTO role (id, name) VALUES (94, 'ROLE_USER');
INSERT INTO user_roles (user_id, role_id) VALUES (99, 96);
INSERT INTO user_roles (user_id, role_id) VALUES (98, 95);
INSERT INTO user_roles (user_id, role_id) VALUES (97, 94);
