-- =========================================================
-- Smart Hostel Attendance System — Database Schema
-- =========================================================
-- 9 tables, normalized, with face embeddings, sessions,
-- and role-based accounts (students / wardens / admins).
-- =========================================================

CREATE DATABASE IF NOT EXISTS hostel_attendance;
USE hostel_attendance;

SET FOREIGN_KEY_CHECKS = 0;


-- ── Table: hostels ───────────────────────────────────────
DROP TABLE IF EXISTS hostels;
CREATE TABLE hostels (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    gender      ENUM('boys','girls') NOT NULL,
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    latitude    DOUBLE NOT NULL,
    longitude   DOUBLE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ── Table: programmes ────────────────────────────────────
DROP TABLE IF EXISTS programmes;
CREATE TABLE programmes (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    programme_name  VARCHAR(50) NOT NULL UNIQUE,
    total_years     INT NOT NULL,
    is_active       TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT programmes_chk_1 CHECK (total_years > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ── Table: rooms ─────────────────────────────────────────
DROP TABLE IF EXISTS rooms;
CREATE TABLE rooms (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    hostel_id   INT NOT NULL,
    room_no     VARCHAR(20) NOT NULL,
    floor_no    INT NOT NULL,
    block       VARCHAR(20) NOT NULL,
    capacity    INT NOT NULL DEFAULT 1,
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    UNIQUE KEY unique_room (hostel_id, room_no),
    CONSTRAINT rooms_ibfk_1 FOREIGN KEY (hostel_id) REFERENCES hostels(id) ON DELETE RESTRICT,
    CONSTRAINT rooms_chk_1 CHECK (capacity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ── Table: students ──────────────────────────────────────
-- FIX APPLIED: email is now a generated column derived from
-- roll_no, exactly like our original design — guarantees a
-- consistent, typo-free college email with zero manual input.
DROP TABLE IF EXISTS students;
CREATE TABLE students (
    roll_no         VARCHAR(15) NOT NULL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(100) GENERATED ALWAYS AS
                    (CONCAT(roll_no, '@nitkkr.ac.in')) STORED UNIQUE,
    password        VARCHAR(255) NOT NULL,
    programme_id    INT NOT NULL,
    year_of_study   INT NOT NULL,
    room_id         INT NOT NULL,
    student_phone   VARCHAR(15) NOT NULL UNIQUE,
    account_status  ENUM('PENDING','FACE_PENDING','ACTIVE','SUSPENDED') NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_programme_id (programme_id),
    KEY idx_room_id (room_id),
    CONSTRAINT students_ibfk_1 FOREIGN KEY (programme_id) REFERENCES programmes(id) ON DELETE RESTRICT,
    CONSTRAINT students_ibfk_2 FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT,
    CONSTRAINT students_chk_1 CHECK (year_of_study > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ── Table: parent_contacts ───────────────────────────────
-- FIX APPLIED: relation is now NOT NULL — every parent
-- contact must specify how they relate to the student.
DROP TABLE IF EXISTS parent_contacts;
CREATE TABLE parent_contacts (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    roll_no       VARCHAR(15) NOT NULL UNIQUE,
    parent_name   VARCHAR(100) NOT NULL,
    parent_phone  VARCHAR(15) NOT NULL,
    relation      ENUM('FATHER','MOTHER','GUARDIAN') NOT NULL,
    CONSTRAINT parent_contacts_ibfk_1 FOREIGN KEY (roll_no) REFERENCES students(roll_no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Trigger: parent phone must differ from the student's own phone
DELIMITER ;;
CREATE TRIGGER check_parent_phone
BEFORE INSERT ON parent_contacts
FOR EACH ROW
BEGIN
    DECLARE s_phone VARCHAR(15);
    SELECT student_phone INTO s_phone
    FROM   students
    WHERE  roll_no = NEW.roll_no;

    IF NEW.parent_phone = s_phone THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Parent phone cannot be same as student phone';
    END IF;
END;;
DELIMITER ;


-- ── Table: face_embeddings ───────────────────────────────
DROP TABLE IF EXISTS face_embeddings;
CREATE TABLE face_embeddings (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    roll_no         VARCHAR(15) NOT NULL,
    photo_path      VARCHAR(255) NOT NULL,
    embedding       JSON NOT NULL,
    is_active       TINYINT(1) NOT NULL DEFAULT 1,
    registered_at   TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    registered_by   ENUM('WARDEN','ADMIN') NOT NULL DEFAULT 'WARDEN',
    deactivated_at  TIMESTAMP NULL DEFAULT NULL,
    KEY idx_embedding_active (roll_no, is_active),
    CONSTRAINT face_embeddings_ibfk_1 FOREIGN KEY (roll_no) REFERENCES students(roll_no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ── Table: attendance_sessions ───────────────────────────
DROP TABLE IF EXISTS attendance_sessions;
CREATE TABLE attendance_sessions (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    session_date  DATE NOT NULL,
    window_start  TIME NOT NULL DEFAULT '08:30:00',
    window_end    TIME NOT NULL DEFAULT '09:30:00',
    hostel_id     INT NOT NULL,
    created_at    TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_session (session_date, hostel_id, window_start),
    KEY idx_hostel_id (hostel_id),
    CONSTRAINT attendance_sessions_ibfk_1 FOREIGN KEY (hostel_id) REFERENCES hostels(id) ON DELETE RESTRICT,
    CONSTRAINT attendance_sessions_chk_1 CHECK (window_start < window_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ── Table: attendance_logs ───────────────────────────────
DROP TABLE IF EXISTS attendance_logs;
CREATE TABLE attendance_logs (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    roll_no     VARCHAR(15) NOT NULL,
    session_id  INT NOT NULL,
    marked_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confidence  DOUBLE DEFAULT NULL,
    latitude    DOUBLE DEFAULT NULL,
    longitude   DOUBLE DEFAULT NULL,
    ip_address  VARCHAR(50) DEFAULT NULL,
    marked_by   ENUM('STUDENT','WARDEN','ADMIN') NOT NULL DEFAULT 'STUDENT',
    UNIQUE KEY one_per_session (roll_no, session_id),
    KEY idx_marked_at (marked_at),
    KEY idx_al_session_id (session_id),
    CONSTRAINT attendance_logs_ibfk_1 FOREIGN KEY (roll_no) REFERENCES students(roll_no) ON DELETE CASCADE,
    CONSTRAINT attendance_logs_ibfk_2 FOREIGN KEY (session_id) REFERENCES attendance_sessions(id) ON DELETE RESTRICT,
    CONSTRAINT attendance_logs_chk_1 CHECK (confidence IS NULL OR (confidence >= 0 AND confidence <= 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ── Table: wardens ───────────────────────────────────────
DROP TABLE IF EXISTS wardens;
CREATE TABLE wardens (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    hostel_id   INT NOT NULL,
    role        ENUM('MAIN_WARDEN','ASSISTANT_WARDEN') NOT NULL,
    phone       VARCHAR(15) NOT NULL UNIQUE,
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    created_at  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_hostel_id (hostel_id),
    CONSTRAINT wardens_ibfk_1 FOREIGN KEY (hostel_id) REFERENCES hostels(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ── Table: admins ────────────────────────────────────────
DROP TABLE IF EXISTS admins;
CREATE TABLE admins (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(15) NOT NULL UNIQUE,
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    created_at  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


SET FOREIGN_KEY_CHECKS = 1;


-- =========================================================
-- Test data
-- =========================================================

INSERT INTO hostels (name, gender, latitude, longitude) VALUES
    ('Kalpana Chawla', 'girls', 29.7180, 76.9930),
    ('Bhagirathi',     'girls', 29.7182, 76.9932),
    ('Cauvery',        'girls', 29.7184, 76.9934),
    ('Alaknanda',      'girls', 29.7186, 76.9936),
    ('H1',  'boys', 29.7190, 76.9940),
    ('H2',  'boys', 29.7192, 76.9942),
    ('H3',  'boys', 29.7194, 76.9944),
    ('H4',  'boys', 29.7196, 76.9946),
    ('H5',  'boys', 29.7198, 76.9948),
    ('H6',  'boys', 29.7200, 76.9950),
    ('H7',  'boys', 29.7202, 76.9952),
    ('H8',  'boys', 29.7204, 76.9954),
    ('H9',  'boys', 29.7206, 76.9956),
    ('H10', 'boys', 29.7208, 76.9958),
    ('H11', 'boys', 29.7210, 76.9960);

INSERT INTO programmes (programme_name, total_years) VALUES
    ('Btech', 4),
    ('Mtech', 2),
    ('MBA',   2),
    ('MCA',   3),
    ('MSc',   2),
    ('PhD',   5);

INSERT INTO rooms (hostel_id, room_no, floor_no, block, capacity) VALUES
    (5, '214', 2, 'A', 2),
    (1, '312', 3, 'B', 2),
    (5, '108', 1, 'A', 3);

INSERT INTO students
    (roll_no, name, password, programme_id, year_of_study, room_id, student_phone)
VALUES
    ('525410037', 'Arjun Kumar',  'hashed_pw', 1, 2, 1, '9876543210'),
    ('525410038', 'Priya Sharma', 'hashed_pw', 1, 2, 2, '9876543212'),
    ('525410039', 'Ravi Patel',   'hashed_pw', 2, 1, 3, '9876543214');

INSERT INTO parent_contacts (roll_no, parent_name, parent_phone, relation) VALUES
    ('525410037', 'Suresh Kumar', '9876543211', 'FATHER'),
    ('525410038', 'Anita Sharma', '9876543213', 'MOTHER'),
    ('525410039', 'Mohan Patel',  '9876543215', 'FATHER');

INSERT INTO wardens (name, email, password, hostel_id, role, phone) VALUES
    ('Dr. Sharma', 'sharma@nitkkr.ac.in', 'hashed_pw', 1, 'MAIN_WARDEN', '9876540001'),
    ('Mr. Verma',  'verma@nitkkr.ac.in',  'hashed_pw', 5, 'MAIN_WARDEN', '9876540002');
