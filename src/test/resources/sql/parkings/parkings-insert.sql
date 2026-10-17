delete from clients_parkings_spots
where client_id in (
    '220e8400-e29b-41d4-a716-446655440001',
    '220e8400-e29b-41d4-a716-446655440002'
);

delete from clients
where id in (
    '220e8400-e29b-41d4-a716-446655440001',
    '220e8400-e29b-41d4-a716-446655440002'
);

delete from parking_spots
where id in (
    '330e8400-e29b-41d4-a716-446655440001',
    '330e8400-e29b-41d4-a716-446655440002'
);

delete from users
where id in (
    '110e8400-e29b-41d4-a716-446655440001',
    '110e8400-e29b-41d4-a716-446655440002',
    '110e8400-e29b-41d4-a716-446655440003'
);

insert into users (id, username, password, role)
values ('110e8400-e29b-41d4-a716-446655440001', 'parking-admin@gmail.com',
        '$2a$12$.3nyflpxjA93BXc/VhkTAezhJTJj0wbUsNUaOBnQ/OGBtb6Q/jeie', 'ROLE_ADMIN');

insert into users (id, username, password, role)
values ('110e8400-e29b-41d4-a716-446655440002', 'parking-client@gmail.com',
        '$2a$12$.3nyflpxjA93BXc/VhkTAezhJTJj0wbUsNUaOBnQ/OGBtb6Q/jeie', 'ROLE_USER');

insert into users (id, username, password, role)
values ('110e8400-e29b-41d4-a716-446655440003', 'other-parking-client@gmail.com',
        '$2a$12$.3nyflpxjA93BXc/VhkTAezhJTJj0wbUsNUaOBnQ/OGBtb6Q/jeie', 'ROLE_USER');

insert into clients (id, name, cpf, user_id)
values ('220e8400-e29b-41d4-a716-446655440001', 'Parking Test Client', '52998224725',
        '110e8400-e29b-41d4-a716-446655440002');

insert into clients (id, name, cpf, user_id)
values ('220e8400-e29b-41d4-a716-446655440002', 'Other Parking Client', '83424546062',
        '110e8400-e29b-41d4-a716-446655440003');

insert into parking_spots (id, code, status)
values ('330e8400-e29b-41d4-a716-446655440001', 'P001', 'OCCUPIED');

insert into parking_spots (id, code, status)
values ('330e8400-e29b-41d4-a716-446655440002', 'P002', 'FREE');

insert into clients_parkings_spots
    (id, number_receipt, brand, model, color, license_plate, entry_date, exit_date, price, discount, client_id, parking_spot_id)
values
    ('440e8400-e29b-41d4-a716-446655440001', '20260101-080001', 'Toyota', 'Corolla', 'Black',
     'OLD-0001', '2026-01-01 08:00:00', '2026-01-01 09:00:00', 9.25, 0.00,
     '220e8400-e29b-41d4-a716-446655440001', '330e8400-e29b-41d4-a716-446655440001');

insert into clients_parkings_spots
    (id, number_receipt, brand, model, color, license_plate, entry_date, exit_date, price, discount, client_id, parking_spot_id)
values
    ('440e8400-e29b-41d4-a716-446655440002', '20260102-080001', 'Honda', 'Civic', 'Silver',
     'OLD-0002', '2026-01-02 08:00:00', '2026-01-02 09:00:00', 9.25, 0.00,
     '220e8400-e29b-41d4-a716-446655440001', '330e8400-e29b-41d4-a716-446655440001');

insert into clients_parkings_spots
    (id, number_receipt, brand, model, color, license_plate, entry_date, exit_date, price, discount, client_id, parking_spot_id)
values
    ('440e8400-e29b-41d4-a716-446655440003', '20261017-100001', 'Ford', 'Focus', 'Blue',
     'ACT-0001', '2026-10-17 10:00:00', null, null, null,
     '220e8400-e29b-41d4-a716-446655440001', '330e8400-e29b-41d4-a716-446655440001');

insert into clients_parkings_spots
    (id, number_receipt, brand, model, color, license_plate, entry_date, exit_date, price, discount, client_id, parking_spot_id)
values
    ('440e8400-e29b-41d4-a716-446655440004', '20260103-080001', 'Nissan', 'Versa', 'White',
     'OTH-0001', '2026-01-03 08:00:00', '2026-01-03 09:00:00', 9.25, 0.00,
     '220e8400-e29b-41d4-a716-446655440002', '330e8400-e29b-41d4-a716-446655440002');
