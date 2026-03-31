- 1. Create the Database
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- 2. Create Table: Members (Users)
-- Stores details of Admins and Students
CREATE TABLE IF NOT EXISTS members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL, -- In a real app, use hashing (e.g., SHA-256)
    role VARCHAR(20) CHECK (role IN ('ADMIN', 'STUDENT')),
    phone_number VARCHAR(15)
);

-- 3. Create Table: Books (Inventory)
-- Stores book details and stock levels
CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    genre VARCHAR(50),
    total_qty INT DEFAULT 1,     -- Total copies owned by library
    available_qty INT DEFAULT 1  -- Copies currently on the shelf
);

-- 4. Create Table: Issue_Records (Transactions)
-- Tracks which member borrowed which book and when
CREATE TABLE IF NOT EXISTS issue_records (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT,
    book_id INT,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE DEFAULT NULL, -- NULL indicates the book is still out
    fine_amount DECIMAL(5, 2) DEFAULT 0.00,
    status VARCHAR(20) DEFAULT 'ISSUED', -- 'ISSUED' or 'RETURNED'
    
    -- Relationships
    FOREIGN KEY (member_id) REFERENCES members(member_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

-- 5. Insert Sample Data (Seed Data)

-- Add Members
INSERT INTO members (full_name, email, password, role, phone_number) VALUES 
('Librarian Admin', 'admin@library.com', 'admin123', 'ADMIN', '1112223333'),
('John Student', 'john@student.com', 'pass123', 'STUDENT', '9998887777'),
('Jane Doe', 'jane@student.com', 'pass123', 'STUDENT', '5556667777');

-- Add Books
INSERT INTO books (title, author, isbn, genre, total_qty, available_qty) VALUES 
('Programming in Java', 'Balagurusamy', '9780070141698', 'Education', 10, 10),
('Clean Code', 'Robert C. Martin', '9780132350884', 'Technology', 5, 5),
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Fiction', 3, 3),
('Data Structures and Algorithms', 'Robert Lafore', '9780672324536', 'Education', 7, 7);

-- Add a sample Issue Record (John borrowed 'Clean Code' yesterday)
INSERT INTO issue_records (member_id, book_id, issue_date, due_date, status) 
VALUES (
    (SELECT member_id FROM members WHERE email='john@student.com'),
    (SELECT book_id FROM books WHERE title='Clean Code'),
    CURDATE() - INTERVAL 1 DAY, -- Issued yesterday
    CURDATE() + INTERVAL 6 DAY, -- Due in 6 days
    'ISSUED'
);

-- Decrease available quantity for the borrowed book
UPDATE books SET available_qty = available_qty - 1 WHERE title = 'Clean Code';
