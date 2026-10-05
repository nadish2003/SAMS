-- ============================================================
-- SAMS - sample_data.sql
-- Run schema.sql first, then run this file.
-- ============================================================

USE sams_db;

-- ============================================================
-- users  (password stored as plain text for coursework)
-- ============================================================
INSERT INTO users (username, password, role) VALUES
    ('admin',    'admin123',    'ADMIN'),
    ('lecturer1','lect123',     'LECTURER'),
    ('lecturer2','lect456',     'LECTURER');

-- ============================================================
-- courses
-- ============================================================
INSERT INTO courses (name, code) VALUES
    ('Higher National Diploma in Software Engineering', 'HND-SE'),
    ('Higher National Diploma in Networking',           'HND-NET');

-- ============================================================
-- subjects
-- ============================================================
INSERT INTO subjects (name, code, course_id) VALUES
    -- HND-SE subjects (course_id = 1)
    ('Object-Oriented Programming', 'OOP',   1),
    ('Database Management Systems', 'DBMS',  1),
    ('Software Engineering',        'SE',    1),
    -- HND-NET subjects (course_id = 2)
    ('Network Fundamentals',        'NETF',  2),
    ('Cyber Security Basics',       'CSB',   2);

-- ============================================================
-- lecturers  (linked to user rows above)
-- ============================================================
INSERT INTO lecturers (name, email, phone, user_id) VALUES
    ('Dr. Amal Perera',   'amal@sams.lk',   '0771234567', 2),
    ('Ms. Niluka Silva',  'niluka@sams.lk', '0779876543', 3);

-- ============================================================
-- lecturer_subjects
-- ============================================================
INSERT INTO lecturer_subjects (lecturer_id, subject_id) VALUES
    (1, 1),   -- Dr. Amal -> OOP
    (1, 2),   -- Dr. Amal -> DBMS
    (2, 3),   -- Ms. Niluka -> SE
    (2, 4),   -- Ms. Niluka -> Network Fundamentals
    (2, 5);   -- Ms. Niluka -> Cyber Security

-- ============================================================
-- students
-- ============================================================
INSERT INTO students (name, registration_number, course_id, email, phone) VALUES
    ('Kasun Bandara',   'HND-SE-001', 1, 'kasun@mail.com',   '0712345601'),
    ('Dilani Fernando', 'HND-SE-002', 1, 'dilani@mail.com',  '0712345602'),
    ('Ruwan Jayasena',  'HND-SE-003', 1, 'ruwan@mail.com',   '0712345603'),
    ('Malini Rathnayake','HND-NET-001',2, 'malini@mail.com',  '0712345604'),
    ('Chamara Wijesinghe','HND-NET-002',2,'chamara@mail.com', '0712345605');

-- ============================================================
-- class_sessions
-- ============================================================
INSERT INTO class_sessions (course_id, subject_id, lecturer_id, session_date, session_time) VALUES
    (1, 1, 1, '2026-10-07', '09:00:00'),   -- OOP class
    (1, 2, 1, '2026-10-08', '11:00:00'),   -- DBMS class
    (2, 4, 2, '2026-10-07', '10:00:00');   -- Network Fundamentals class

-- ============================================================
-- attendance  (pre-seed a couple of records)
-- ============================================================
INSERT INTO attendance (student_id, class_session_id, status) VALUES
    (1, 1, 'PRESENT'),
    (2, 1, 'LATE'),
    (3, 1, 'ABSENT'),
    (4, 3, 'PRESENT'),
    (5, 3, 'PRESENT');
