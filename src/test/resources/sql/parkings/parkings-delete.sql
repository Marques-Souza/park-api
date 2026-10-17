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
