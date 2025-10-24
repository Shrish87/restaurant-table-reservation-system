-- -- Drop tables if they exist (in order to avoid FK errors)
-- DROP TABLE IF EXISTS Reservation;
-- DROP TABLE IF EXISTS Customer;
-- DROP TABLE IF EXISTS Restaurant_Table;
--
-- -- Create Customer table
-- CREATE TABLE Customer (
--     customer_id INT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(100) NOT NULL,
--     email VARCHAR(100) NOT NULL UNIQUE,
--     phone VARCHAR(20) NOT NULL,
--     created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );
--
-- -- Create Restaurant_Table table
-- CREATE TABLE Restaurant_Table (
--     table_id INT AUTO_INCREMENT PRIMARY KEY,
--     table_number INT NOT NULL UNIQUE,
--     capacity INT NOT NULL CHECK (capacity > 0),
--     location VARCHAR(50) NOT NULL,
--     is_available CHAR(1) DEFAULT 'Y' CHECK (is_available IN ('Y','N'))
-- );
--
-- -- Create Reservation table
-- CREATE TABLE Reservation (
--     reservation_id INT AUTO_INCREMENT PRIMARY KEY,
--     customer_id INT NOT NULL,
--     table_id INT NOT NULL,
--     reservation_date DATE NOT NULL,
--     time_slot VARCHAR(20) NOT NULL,
--     number_of_guests INT NOT NULL CHECK (number_of_guests > 0),
--     status VARCHAR(20) DEFAULT 'CONFIRMED' CHECK (status IN ('CONFIRMED', 'CANCELLED', 'COMPLETED')),
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
--     CONSTRAINT fk_table FOREIGN KEY (table_id) REFERENCES Restaurant_Table(table_id),
--     -- Unique constraint to prevent double booking for confirmed reservations
--     CONSTRAINT uk_reservation UNIQUE (table_id, reservation_date, time_slot, status)
-- );

-- Insert sample data for Restaurant_Table with location (non-null)
INSERT INTO Restaurant_Table (table_id, table_number, capacity, location, is_available) VALUES
                    (1, 1, 2, 'Window Side', 'Y'),
                    (2, 2, 4, 'Main Hall', 'Y'),
                    (3, 3, 4, 'Main Hall', 'N'),
                    (4, 4, 6, 'Private Room', 'Y'),
                    (5, 5, 8, 'Banquet Hall', 'Y'),
                    (6, 6, 2, 'Balcony', 'Y');

-- Commit is optional in MySQL (autocommit on by default)
