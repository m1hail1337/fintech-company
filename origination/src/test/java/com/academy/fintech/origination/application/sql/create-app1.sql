INSERT INTO Client (id, first_name, last_name, email, salary)
VALUES ('cl12', 'Mihail', 'Semenov', 'example@mail.ru', 10000.00);

INSERT INTO Client (id, first_name, last_name, email, salary)
VALUES ('cl22', 'Ivan', 'Ivanov', 'some@mail.com', 120000.00);

INSERT INTO Application (id, client_id, requested_disbursement_amount, status)
VALUES ('app1', 'cl1', 10000.00, 'NEW');

INSERT INTO Application (id, client_id, requested_disbursement_amount, status)
VALUES ('app3', 'cl2', 120000.00, 'ACTIVE');