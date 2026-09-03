INSERT INTO employees (employee_id, full_name, department) VALUES
    ('EMP-210', 'Nguyen Minh Anh', 'Kinh doanh'),
    ('EMP-211', 'Tran Quoc Bao', 'Van hanh'),
    ('EMP-212', 'Le Thu Chau', 'Khach hang')
ON CONFLICT (employee_id) DO UPDATE
SET full_name = EXCLUDED.full_name,
    department = EXCLUDED.department;

INSERT INTO room_types (room_type, display_name, max_guests, base_price, active) VALUES
    ('STD', 'Standard', 2, 900000.00, TRUE),
    ('DLX', 'Deluxe', 2, 1300000.00, TRUE),
    ('STE', 'Suite', 4, 2400000.00, TRUE)
ON CONFLICT (room_type) DO UPDATE
SET display_name = EXCLUDED.display_name,
    max_guests = EXCLUDED.max_guests,
    base_price = EXCLUDED.base_price,
    active = EXCLUDED.active;

INSERT INTO room_inventory (room_type, available_date, available_rooms) VALUES
    ('STD', DATE '2026-09-15', 8), ('STD', DATE '2026-09-16', 8), ('STD', DATE '2026-09-17', 7),
    ('DLX', DATE '2026-09-15', 3), ('DLX', DATE '2026-09-16', 3), ('DLX', DATE '2026-09-17', 2),
    ('STE', DATE '2026-09-15', 1), ('STE', DATE '2026-09-16', 1), ('STE', DATE '2026-09-17', 1)
ON CONFLICT (room_type, available_date) DO UPDATE
SET available_rooms = EXCLUDED.available_rooms;

INSERT INTO booking_requests
    (request_id, employee_id, room_type, check_in, check_out, guest_count, purpose, status, decision_note, created_at, updated_at)
VALUES
    ('BR-1001', 'EMP-210', 'DLX', DATE '2026-09-15', DATE '2026-09-17', 1, 'Cong tac gap doi tac Ha Noi', 'PENDING', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('BR-1002', 'EMP-211', 'STD', DATE '2026-09-20', DATE '2026-09-21', 2, 'Dao tao van hanh khu vuc', 'APPROVED', 'Da duyet', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('BR-1003', 'EMP-212', 'STE', DATE '2026-09-22', DATE '2026-09-25', 3, 'Tiep doan khach hang doanh nghiep', 'PENDING', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (request_id) DO UPDATE
SET status = EXCLUDED.status,
    decision_note = EXCLUDED.decision_note,
    updated_at = CURRENT_TIMESTAMP;
