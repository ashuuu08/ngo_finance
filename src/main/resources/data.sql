-- NGO Finance Seed Data
-- 1. Usuarios (Handled dynamically in DataInitializer.java)

-- 2. Sample Donations (Income)
INSERT INTO donations (transaction_id, u_name, amount, date_and_time, updated_by) VALUES 
('DON-A1B2C3D4', 'John Smith', 500.00, CURRENT_TIMESTAMP, 'admin@ngo.in'),
('DON-E5F6G7H8', 'Alice Doe', 1200.00, CURRENT_TIMESTAMP, 'admin@ngo.in'),
('DON-I9J0K1L2', 'Corporate Sponsor XYZ', 5000.00, CURRENT_TIMESTAMP, 'admin@ngo.in'),
('DON-M3N4O5P6', 'Mark Wilson', 150.00, CURRENT_TIMESTAMP, 'admin@ngo.in');

-- 3. Sample Expenses (Outflow)
INSERT INTO expenses (transaction_id, category, amount, description, time_and_date, updated_by) VALUES 
('EXP-Z1Y2X3W4', 'OFFICE_SUPPLIES', 200.00, 'Purchased paper and toner', CURRENT_TIMESTAMP, 'admin@ngo.in'),
('EXP-V5U6T7S8', 'LOGISTICS', 450.00, 'Fuel and transport for outreach', CURRENT_TIMESTAMP, 'admin@ngo.in'),
('EXP-R9Q0P1O2', 'UTILITIES', 300.00, 'Electricity bill payment', CURRENT_TIMESTAMP, 'admin@ngo.in'),
('EXP-N3M4L5K6', 'MARKETING', 1000.00, 'Social media awareness campaign', CURRENT_TIMESTAMP, 'admin@ngo.in');
