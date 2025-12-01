-- Seed data with explicit owners so ownership rules can be enforced in tests
INSERT INTO CASH_CARD(ID, AMOUNT, OWNER) VALUES (99, 123.45, 'sarah1');
INSERT INTO CASH_CARD(ID, AMOUNT, OWNER) VALUES (100, 1.00, 'sarah1');
INSERT INTO CASH_CARD(ID, AMOUNT, OWNER) VALUES (101, 150.00, 'sarah1');
-- Additional card owned by kumar, used in cross-ownership tests
INSERT INTO CASH_CARD(ID, AMOUNT, OWNER) VALUES (102, 200.00, 'kumar2');


