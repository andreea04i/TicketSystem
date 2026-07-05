INSERT INTO tickets (
    ticket_number,
    title,
    description,
    category,
    priority,
    status,
    sla_breached,
    created_at,
    updated_at
)
VALUES
(
    'HD-1001',
    'Laptopul nu mai pornește',
    'Laptopul afișează un ecran negru la pornire.',
    'IT',
    'CRITICAL',
    'OPEN',
    TRUE,
    CURRENT_TIMESTAMP - INTERVAL '5 hours',
    CURRENT_TIMESTAMP - INTERVAL '5 hours'
),
(
    'HD-1002',
    'Eroare la generarea raportului financiar',
    'Raportul financiar nu se generează.',
    'FINANCIAL',
    'HIGH',
    'IN_PROGRESS',
    FALSE,
    CURRENT_TIMESTAMP - INTERVAL '4 hours',
    CURRENT_TIMESTAMP - INTERVAL '2 hours'
),
(
    'HD-1003',
    'Acces blocat la cont',
    'Utilizatorul nu se poate autentifica.',
    'IT',
    'HIGH',
    'OPEN',
    FALSE,
    CURRENT_TIMESTAMP - INTERVAL '3 hours',
    CURRENT_TIMESTAMP - INTERVAL '3 hours'
),
(
    'HD-1004',
    'Actualizare date contract',
    'Este necesară corectarea departamentului.',
    'HR',
    'MEDIUM',
    'ESCALATED',
    FALSE,
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
),
(
    'HD-1005',
    'Imprimanta nu funcționează',
    'Imprimanta nu mai răspunde la comenzi.',
    'IT',
    'MEDIUM',
    'OPEN',
    FALSE,
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
),
(
    'HD-1006',
    'Întrebare despre salariu',
    'Angajatul solicită clarificări.',
    'FINANCIAL',
    'LOW',
    'RESOLVED',
    FALSE,
    CURRENT_TIMESTAMP - INTERVAL '3 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
);