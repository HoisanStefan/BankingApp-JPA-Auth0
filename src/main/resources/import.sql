insert into bank(id, name) values(1, 'Banca Transilvania');

insert into bank(id, name) values(2, 'BRD');

ALTER TABLE bank ALTER COLUMN id RESTART WITH 3;

insert into client(id, uid) values(1, 'auth0|640f16de64d7d23a2ba9710c');

insert into client(id, uid) values(2, 'auth0|640f115efda06590890a7bdb');

insert into client(id, uid) values(3, 'auth0|641b655440ee26f5cdca3c3b');

ALTER TABLE client ALTER COLUMN id RESTART WITH 4;

insert into contract(id, bank_id, client_id, creation_date) values (1, 1, 2, '2023-03-11 14:30:00');

insert into contract(id, bank_id, client_id, creation_date) values (2, 2, 2, NOW());

ALTER TABLE contract ALTER COLUMN id RESTART WITH 3;

insert into account(id, bank_id, client_id, account_number, account_type) values (1, 1, 2, 'RO49BCER1B31007593840000', 'CURRENT');

insert into account(id, bank_id, client_id, account_number, account_type) values (2, 2, 2, 'RO41BCER123ASF32SAF12121', 'CURRENT');

insert into account(id, bank_id, client_id, account_number, account_type) values (3, 2, 2, 'RO43BCER123HGF32SAF16423', 'DEPOSIT');

ALTER TABLE account ALTER COLUMN id RESTART WITH 4;

insert into card(id, account_id, card_number, cvv) values (1, 1, '4539015906336821', '846');

insert into card(id, account_id, card_number, cvv) values (2, 2, '4916586506660904', '536');

insert into card(id, account_id, card_number, cvv) values (3, 3, '4539778670851230', '381');

ALTER TABLE card ALTER COLUMN id RESTART WITH 4;

insert into credit(id, bank_id, amount_total, credit_state) values (1, 1, 100000.0, 'NEW');

insert into credit(id, bank_id, amount_total, credit_state) values (2, 1, 120000.0, 'NEW');

insert into credit(id, bank_id, amount_total, credit_state) values (3, 1, 150000.0, 'NEW');

insert into credit(id, bank_id, amount_total, credit_state) values (4, 2, 110000.0, 'NEW');

insert into credit(id, bank_id, amount_total, credit_state) values (5, 2, 130000.0, 'NEW');

insert into credit(id, bank_id, amount_total, credit_state) values (6, 2, 140000.0, 'NEW');

insert into credit(id, bank_id, client_id, amount_paid, amount_total, credit_state) values (7, 1, 2, 100000.0, 100000.0, 'PAID');

insert into credit(id, bank_id, client_id, amount_paid, amount_total, credit_state) values (8, 1, 2, 123.45, 100000.0, 'UNPAID');

insert into credit(id, bank_id, client_id, amount_paid, amount_total, credit_state) values (9, 2, 2, 13000.43, 130000.0, 'UNPAID');

ALTER TABLE credit ALTER COLUMN id RESTART WITH 10;

insert into deposit(id, username, bank_name, deposit_value, account_number) values (1, 'Popescu Andrei', 'BRD', 12000.00, 'RO41BCER123ASF32SAF12121');

insert into deposit(id, username, bank_name, deposit_value, account_number) values (2, 'Popescu Andrei', 'Banca Transilvania', 123000.00, 'RO49BCER1B31007593840000');

insert into deposit(id, username, bank_name, deposit_value, account_number) values (3, 'Popescu Andrei', 'BRD', 4023.23, 'RO43BCER123HGF32SAF16423');

ALTER TABLE deposit ALTER COLUMN id RESTART WITH 4;

insert into payment(id, username, bank_name, paid_value, account_number) values (1, 'Popescu Andrei', 'Banca Transilvania', 100000.00, 'RO49BCER1B31007593840000');

insert into payment(id, username, bank_name, paid_value, account_number) values (2, 'Popescu Andrei', 'Banca Transilvania', 123.45, 'RO49BCER1B31007593840000');

insert into payment(id, username, bank_name, paid_value, account_number) values (3, 'Popescu Andrei', 'BRD', 10000.00, 'RO41BCER123ASF32SAF12121');

insert into payment(id, username, bank_name, paid_value, account_number) values (4, 'Popescu Andrei', 'BRD', 3000.43, 'RO43BCER123HGF32SAF16423');

ALTER TABLE payment ALTER COLUMN id RESTART WITH 5;

UPDATE account a SET a.account_value = 22876.55 WHERE id = 1;

UPDATE account a SET a.account_value = 2000.00 WHERE id = 2;

UPDATE account a SET a.account_value = 1022.80 WHERE id = 3;