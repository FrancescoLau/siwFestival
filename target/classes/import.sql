-- Utente 1: Amministratore (ADMIN)
-- Username: admin | Password: admin123
INSERT INTO users (id, nome, cognome, email)  VALUES (nextval('users_seq'), 'Admin', 'Admin', 'admin@festival.com');

INSERT INTO credentials (id, username, password, role, user_id)  VALUES (nextval('credentials_seq'), 'admin', '$2a$10$e9YqiHzV2gP/4IoHclJ3MOAO5G9Ze1T6u/dlOvb8vt8RcGMrJFYEG', 'ADMIN', currval('users_seq'));

-- Utente 2: Utente Standard (USER)
-- Username: user | Password: user123
INSERT INTO users (id, nome, cognome, email) VALUES (nextval('users_seq'), 'User', 'User', 'user@festival.com');

INSERT INTO credentials (id, username, password, role, user_id) VALUES (nextval('credentials_seq'), 'user', '$2a$10$0/hPct8zRm1Rdfr4vftzMOJiLPbJNJ.0OY2eEHzS5KPBJE6C.LpO6', 'DEFAULT', currval('users_seq'));