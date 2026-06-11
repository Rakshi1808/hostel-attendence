-- database/schema.sql
-- Smart Hostel Attendance System
-- Run this once to set up the database

CREATE DATABASE IF NOT EXISTS hostel_attendance;
USE hostel_attendance;


-- ── Table 1: students ────────────────────────────────
CREATE TABLE IF NOT EXISTS students (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    student_id   VARCHAR(20)  NOT NULL UNIQUE,  -- e.g. "S101"
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,          -- hashed by Spring Boot
    room_no      INT          NOT NULL,
    floor_no     INT          NOT NULL,
    block        VARCHAR(5)   NOT NULL,
    photo_path   VARCHAR(255),                   -- "photos/S101.jpg"
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ── Table 2: attendance_logs ─────────────────────────
CREATE TABLE IF NOT EXISTS attendance_logs (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    student_id   VARCHAR(20)  NOT NULL,
    marked_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status       ENUM('present', 'absent') DEFAULT 'present',
    confidence   DECIMAL(5,4),                   -- face match score e.g. 0.9423
    ip_address   VARCHAR(50),                    -- for WiFi audit trail
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);


-- ── Indexes for fast queries ─────────────────────────
-- warden dashboard queries by date constantly
CREATE INDEX idx_marked_at    ON attendance_logs(marked_at);
-- finding one student's history is common
CREATE INDEX idx_student_id   ON attendance_logs(student_id);


-- ── Test data — 3 sample students ────────────────────
INSERT INTO students (student_id, name, email, password, room_no, floor_no, block)
VALUES
    ('S101', 'Arjun Kumar',  'arjun@hostel.com',  'hashed_pw', 214, 2, 'B'),
    ('S102', 'Priya Sharma', 'priya@hostel.com',  'hashed_pw', 312, 3, 'A'),
    ('S103', 'Ravi Patel',   'ravi@hostel.com',   'hashed_pw', 108, 1, 'C');