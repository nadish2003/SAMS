-- ============================================================
-- SAMS - Student Attendance Management System
-- schema.sql  -  run this first to create all tables
-- ============================================================

CREATE DATABASE IF NOT EXISTS sams_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE sams_db;

-- ============================================================
-- 1. users  (Admin and Lecturer accounts)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id       INT          NOT NULL AUTO_INCREMENT,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,          -- store plain text for coursework; hash in production
    role     ENUM('ADMIN', 'LECTURER') NOT NULL DEFAULT 'LECTURER',
    PRIMARY KEY (id)
);

-- ============================================================
-- 2. courses
-- ============================================================
CREATE TABLE IF NOT EXISTS courses (
    id   INT         NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20)  NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

-- ============================================================
-- 3. subjects  (each subject belongs to one course)
-- ============================================================
CREATE TABLE IF NOT EXISTS subjects (
    id        INT         NOT NULL AUTO_INCREMENT,
    name      VARCHAR(100) NOT NULL,
    code      VARCHAR(20)  NOT NULL UNIQUE,
    course_id INT         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_subject_course FOREIGN KEY (course_id)
        REFERENCES courses (id) ON DELETE CASCADE
);

-- ============================================================
-- 4. students  (each student is enrolled in one course)
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    id                  INT         NOT NULL AUTO_INCREMENT,
    name                VARCHAR(100) NOT NULL,
    registration_number VARCHAR(30)  NOT NULL UNIQUE,
    course_id           INT         NOT NULL,
    email               VARCHAR(100),
    phone               VARCHAR(20),
    PRIMARY KEY (id),
    CONSTRAINT fk_student_course FOREIGN KEY (course_id)
        REFERENCES courses (id) ON DELETE RESTRICT
);

-- ============================================================
-- 5. lecturers  (linked to a user account)
-- ============================================================
CREATE TABLE IF NOT EXISTS lecturers (
    id      INT         NOT NULL AUTO_INCREMENT,
    name    VARCHAR(100) NOT NULL,
    email   VARCHAR(100),
    phone   VARCHAR(20),
    user_id INT         NOT NULL UNIQUE,    -- one user per lecturer
    PRIMARY KEY (id),
    CONSTRAINT fk_lecturer_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

-- ============================================================
-- 6. lecturer_subjects  (a lecturer can teach many subjects)
-- ============================================================
CREATE TABLE IF NOT EXISTS lecturer_subjects (
    lecturer_id INT NOT NULL,
    subject_id  INT NOT NULL,
    PRIMARY KEY (lecturer_id, subject_id),
    CONSTRAINT fk_ls_lecturer FOREIGN KEY (lecturer_id)
        REFERENCES lecturers (id) ON DELETE CASCADE,
    CONSTRAINT fk_ls_subject FOREIGN KEY (subject_id)
        REFERENCES subjects (id) ON DELETE CASCADE
);

-- ============================================================
-- 7. class_sessions  (scheduled class events)
-- ============================================================
CREATE TABLE IF NOT EXISTS class_sessions (
    id           INT  NOT NULL AUTO_INCREMENT,
    course_id    INT  NOT NULL,
    subject_id   INT  NOT NULL,
    lecturer_id  INT  NOT NULL,
    session_date DATE NOT NULL,
    session_time TIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_cs_course   FOREIGN KEY (course_id)   REFERENCES courses   (id) ON DELETE RESTRICT,
    CONSTRAINT fk_cs_subject  FOREIGN KEY (subject_id)  REFERENCES subjects  (id) ON DELETE RESTRICT,
    CONSTRAINT fk_cs_lecturer FOREIGN KEY (lecturer_id) REFERENCES lecturers (id) ON DELETE RESTRICT
);

-- ============================================================
-- 8. attendance  (one row per student per session)
-- ============================================================
CREATE TABLE IF NOT EXISTS attendance (
    id               INT  NOT NULL AUTO_INCREMENT,
    student_id       INT  NOT NULL,
    class_session_id INT  NOT NULL,
    status           ENUM('PRESENT', 'ABSENT', 'LATE') NOT NULL DEFAULT 'ABSENT',
    marked_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_attendance (student_id, class_session_id),   -- one record per student per session
    CONSTRAINT fk_att_student FOREIGN KEY (student_id)
        REFERENCES students (id) ON DELETE CASCADE,
    CONSTRAINT fk_att_session FOREIGN KEY (class_session_id)
        REFERENCES class_sessions (id) ON DELETE CASCADE
);
