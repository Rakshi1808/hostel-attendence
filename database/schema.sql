-- database/schema.sql
-- Smart Hostel Attendance System — fully normalized

CREATE DATABASE IF NOT EXISTS hostel_attendance;
USE hostel_attendance;


-- ── Table 1: hostels ──────────────────────────────────
-- stores hostel names and gender type
-- nothing else — hostel has one job
CREATE TABLE IF NOT EXISTS hostels (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(50)          NOT NULL UNIQUE,
    gender  ENUM('boys','girls') NOT NULL
);

INSERT INTO hostels (name, gender) VALUES
    ('Kalpana Chawla', 'girls'),
    ('Bhagirathi',     'girls'),
    ('Cauvery',        'girls'),
    ('Alaknanda',      'girls'),
    ('H1',  'boys'), ('H2',  'boys'), ('H3',  'boys'),
    ('H4',  'boys'), ('H5',  'boys'), ('H6',  'boys'),
    ('H7',  'boys'), ('H8',  'boys'), ('H9',  'boys'),
    ('H10', 'boys'), ('H11', 'boys');


-- ── Table 2: programmes ───────────────────────────────
-- stores programme names and duration
-- year_of_study in students is validated against total_years
CREATE TABLE IF NOT EXISTS programmes (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(20) NOT NULL UNIQUE,
    total_years INT         NOT NULL
);

INSERT INTO programmes (name, total_years) VALUES
    ('Btech', 4),
    ('Mtech', 2),
    ('MBA',   2),
    ('MCA',   3),
    ('MSc',   2),
    ('PhD',   5);


-- ── Table 3: rooms ────────────────────────────────────
-- room details separated from student
-- one room can have multiple students (shared rooms)
-- changing room details only needs one row update
CREATE TABLE IF NOT EXISTS rooms (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    hostel_id  INT         NOT NULL,
    room_no    INT         NOT NULL,
    floor_no   INT         NOT NULL,
    block      VARCHAR(5)  NOT NULL,
    capacity   INT         NOT NULL DEFAULT 2,

    -- same room number can exist in different hostels
    -- but not twice in the same hostel
    UNIQUE KEY unique_room (hostel_id, room_no),
    FOREIGN KEY (hostel_id) REFERENCES hostels(id)
);

INSERT INTO rooms (hostel_id, room_no, floor_no, block, capacity) VALUES
    (1, 101, 1, 'A', 2),
    (1, 102, 1, 'A', 2),
    (1, 214, 2, 'B', 2),
    (5, 108, 1, 'A', 3),
    (5, 312, 3, 'C', 2);


-- ── Table 4: students ─────────────────────────────────
-- core student identity only
-- links to room, programme via foreign keys
-- no repeated data
CREATE TABLE IF NOT EXISTS students (
    roll_no        VARCHAR(15)  NOT NULL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    email          VARCHAR(100) GENERATED ALWAYS AS
                   (CONCAT(roll_no, '@nitkkr.ac.in')) STORED,
    password       VARCHAR(255) NOT NULL,
    programme_id   INT          NOT NULL,
    year_of_study  INT          NOT NULL,
    room_id        INT          NOT NULL,
    student_phone  VARCHAR(15)  NOT NULL UNIQUE,
    photo_path     VARCHAR(255),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (programme_id) REFERENCES programmes(id),
    FOREIGN KEY (room_id)      REFERENCES rooms(id)
);


-- ── Table 5: parent_contacts ──────────────────────────
-- parent info separated from student
-- one row per student
-- parent phone guaranteed different from student phone
-- via cross table check in application layer
CREATE TABLE IF NOT EXISTS parent_contacts (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    roll_no       VARCHAR(15)  NOT NULL UNIQUE,
    parent_name   VARCHAR(100) NOT NULL,
    parent_phone  VARCHAR(15)  NOT NULL,
    relation      ENUM('father','mother','guardian') NOT NULL,

    -- parent phone must differ from student phone
    -- enforced by trigger below
    FOREIGN KEY (roll_no) REFERENCES students(roll_no)
);


-- ── Trigger: parent phone != student phone ────────────
DELIMITER //
CREATE TRIGGER check_parent_phone
BEFORE INSERT ON parent_contacts
FOR EACH ROW
BEGIN
    DECLARE s_phone VARCHAR(15);
    SELECT student_phone INTO s_phone
    FROM students
    WHERE roll_no = NEW.roll_no;

    IF NEW.parent_phone = s_phone THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Parent phone cannot be same as student phone';
    END IF;
END;
//
DELIMITER ;


-- ── Table 6: wardens ──────────────────────────────────
-- warden login and hostel assignment
-- one warden per hostel (can be changed to many later)
CREATE TABLE IF NOT EXISTS wardens (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    hostel_id  INT          NOT NULL UNIQUE,
    phone      VARCHAR(15)  NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (hostel_id) REFERENCES hostels(id)
);


-- ── Table 7: attendance_logs ──────────────────────────
-- one row per attendance mark
-- links to student via roll_no
CREATE TABLE IF NOT EXISTS attendance_logs (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    roll_no     VARCHAR(15)  NOT NULL,
    marked_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status      ENUM('present','absent') DEFAULT 'present',
    confidence  DECIMAL(5,4),
    ip_address  VARCHAR(50),

    FOREIGN KEY (roll_no) REFERENCES students(roll_no)
);


-- ── Indexes ───────────────────────────────────────────
CREATE INDEX idx_marked_at ON attendance_logs(marked_at);
CREATE INDEX idx_roll_no   ON attendance_logs(roll_no);
CREATE INDEX idx_room_id   ON students(room_id);


-- ── Test data ─────────────────────────────────────────
INSERT INTO students
    (roll_no, name, password, programme_id, year_of_study, room_id, student_phone)
VALUES
    ('525410037', 'Arjun Kumar',  'hashed_pw', 1, 2, 3, '9876543210'),
    ('525410038', 'Priya Sharma', 'hashed_pw', 1, 2, 5, '9876543212'),
    ('525410039', 'Ravi Patel',   'hashed_pw', 2, 1, 4, '9876543214');

INSERT INTO parent_contacts (roll_no, parent_name, parent_phone, relation)
VALUES
    ('525410037', 'Suresh Kumar',  '9876543211', 'father'),
    ('525410038', 'Anita Sharma',  '9876543213', 'mother'),
    ('525410039', 'Mohan Patel',   '9876543215', 'father');

INSERT INTO wardens (name, email, password, hostel_id, phone)
VALUES
    ('Dr. Sharma', 'sharma@nitkkr.ac.in', 'hashed_pw', 1, '9876540001'),
    ('Mr. Verma',  'verma@nitkkr.ac.in',  'hashed_pw', 5, '9876540002');git add .