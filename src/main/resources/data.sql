--CLIENT--
INSERT INTO client (id, first_name, last_name, email, phone_number, address)
VALUES (1, 'Jan', 'Kowalski', 'jan.kowalski@example.com', '123456789', 'ul. Kwiatowa 5, 00-123 Warszawa');
INSERT INTO client (id, first_name, last_name, email, phone_number, address)
VALUES (2, 'Anna', 'Nowak', 'anna.nowak@example.com', '987654321', 'ul. Leśna 12, 40-200 Katowice');
INSERT INTO client (id, first_name, last_name, email, phone_number, address)
VALUES (3, 'Piotr', 'Wiśniewski', 'piotr.wisniewski@example.com', '555111222', 'ul. Główna 10, 30-001 Kraków');

--PACKAGE--
INSERT INTO parcel (id, content_description, sender_address, recipient_address, dispatch_date, delivery_date, weight, price, client_id)
VALUES (1, 'Dokumenty firmowe', 'ul. Kwiatowa 5, 00-123 Warszawa', 'ul. Polna 2, 00-321 Warszawa', TIMESTAMP '2025-04-24 08:00:00', TIMESTAMP '2025-04-24 12:00:00', 0.5, 25.00, 1);
INSERT INTO parcel (id, content_description, sender_address, recipient_address, dispatch_date, delivery_date, weight, price, client_id)
VALUES (2, 'Prezent urodzinowy', 'ul. Leśna 12, 40-200 Katowice', 'ul. Długa 77, 00-500 Warszawa', TIMESTAMP '2025-04-23 15:00:00', TIMESTAMP '2025-04-25 10:00:00', 2.0, 50.00, 2);
INSERT INTO parcel (id, content_description, sender_address, recipient_address, dispatch_date, delivery_date, weight, price, client_id)
VALUES (3, 'Laptop do serwisu', 'ul. Główna 10, 30-001 Kraków', 'ul. Serwisowa 5, 60-001 Poznań', TIMESTAMP '2025-04-22 09:00:00', TIMESTAMP '2025-04-24 14:00:00', 3.5, 70.00, 3);
INSERT INTO parcel (id, content_description, sender_address, recipient_address, dispatch_date, delivery_date, weight, price, client_id)
VALUES (4, 'Dokumenty firmowe', 'ul. Kwiatowa 5, 00-123 Warszawa', 'ul. Polna 2, 00-321 Warszawa', TIMESTAMP '2025-04-24 08:00:00', TIMESTAMP '2025-04-24 12:00:00', 0.5, 25.00, 2);

alter sequence CLIENT_SEQ restart with 100;
alter sequence PARCEL_SEQ restart with 100;