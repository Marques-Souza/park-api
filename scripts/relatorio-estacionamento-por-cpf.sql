SELECT
    clients.cpf,
    clients.name AS nome,
    clients_parkings_spots.color AS cor,
    clients_parkings_spots.discount AS desconto,
    clients_parkings_spots.brand AS marca,
    clients_parkings_spots.model AS modelo,
    clients_parkings_spots.license_plate AS placa,
    clients_parkings_spots.number_receipt AS numero_recibo,
    clients_parkings_spots.price AS valor,
    parking_spots.code AS codigo_vaga,
    clients_parkings_spots.entry_date AS data_entrada,
    clients_parkings_spots.exit_date AS data_saida,
    CAST(FLOOR(DATE_PART('epoch', clients_parkings_spots.exit_date - clients_parkings_spots.entry_date) / 3600) AS INTEGER) AS hours,
    CAST(FLOOR(DATE_PART('epoch', clients_parkings_spots.exit_date - clients_parkings_spots.entry_date) / 60) AS INTEGER) AS minutes
FROM clients_parkings_spots
INNER JOIN clients
    ON clients_parkings_spots.client_id = clients.id
INNER JOIN parking_spots
    ON clients_parkings_spots.parking_spot_id = parking_spots.id
WHERE clients.cpf = 'PREENCHA_COM_UM_CPF_PARA_TESTE'
ORDER BY clients_parkings_spots.license_plate;
