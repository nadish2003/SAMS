# SAMS — Student Attendance Management System

A desktop application developed with Java, JavaFX, and MySQL to streamline attendance tracking and academic session management in educational institutions. SAMS incorporates role-based access control for Administrators and Lecturers, real-time attendance marking, conflict detection, and comprehensive filtering reports.

---

## 1. Project Overview

SAMS (Student Attendance Management System) simplifies student record-keeping and attendance monitoring. It allows administrators to configure courses, subjects, students, lecturers, and class timetables, while providing lecturers with a dedicated interface to view their schedules, record student attendance (Present, Absent, Late), and analyze attendance percentages.

---

## 2. Technologies Used

- **Programming Language**: Java 17 / 21
- **Graphical User Interface**: JavaFX 21.0.2 (JavaFX Controls, JavaFX FXML, JavaFX Graphics)
- **Database Engine**: MySQL Server 8.x
- **Database Connectivity**: JDBC via MySQL Connector/J 8.3.0
- **Build Tool**: Apache Maven (Compiler plugin & JavaFX Maven plugin)
- **Architecture**: Layered Architecture (`model` -> `dao` -> `service` -> `controller` -> FXML / CSS)

---

## 3. Setup Instructions

### Prerequisites
1. **Java Development Kit (JDK)**: JDK 17 or higher installed and configured on your system PATH (`java -version`).
2. **MySQL Server**: MySQL Server 8.x running locally on port 3306.
3. **Apache Maven**: Maven 3.8+ (optional if using an IDE like IntelliJ IDEA or Eclipse).

### Database Configuration
Database connection settings are configured in:
`src/main/java/com/sams/util/DBConnection.java`

Default settings:
```java
private static final String DB_URL      = "jdbc:mysql://localhost:3306/sams_db?useSSL=false&serverTimezone=UTC";
private static final String DB_USER     = "root";
private static final String DB_PASSWORD = "root";
```
Update `DB_USER` and `DB_PASSWORD` if your local MySQL installation uses different credentials.

---

## 4. MySQL Database Setup

1. Open your MySQL client (MySQL Workbench, MySQL Command Line Client, or phpMyAdmin).
2. Execute the database initialization script to create the database and tables:
   ```sql
   source database/schema.sql;
   ```
   *(Creates `sams_db` and tables: `users`, `courses`, `subjects`, `students`, `lecturers`, `lecturer_subjects`, `class_sessions`, `attendance` with foreign key constraints).*
3. Populate the sample seed data:
   ```sql
   source database/sample_data.sql;
   ```

---

## 5. How to Run the JavaFX Application

### Option A: Using Maven (Recommended)
Run the following command from the project root (`SAMS-main`):
```bash
mvn clean javafx:run
```

### Option B: Using an IDE (IntelliJ IDEA / Eclipse / NetBeans)
1. Open the `SAMS-main` directory as a Maven project.
2. Ensure Project SDK is set to JDK 17 or JDK 21.
3. Locate `src/main/java/com/sams/Main.java`.
4. Run or Debug the `Main` class.

### Option C: Direct Compilation and Execution
```powershell
# Compile classes into target/classes
javac -cp "libs/*;src/main/java" (Get-ChildItem -Recurse src\main\java -Filter "*.java" | ForEach-Object { $_.FullName }) -d target/classes

# Run JavaFX application
java --module-path <path-to-javafx-sdk>/lib --add-modules javafx.controls,javafx.fxml -cp "target/classes;<path-to-mysql-connector-jar>" com.sams.Main
```

---

## 6. Default Login Credentials

The sample database comes pre-configured with the following user accounts:

| Username | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | **ADMIN** | System Administrator with full access |
| `lecturer1` | `lect123` | **LECTURER** | Dr. Amal Perera (Assigned to OOP, DBMS) |
| `lecturer2` | `lect456` | **LECTURER** | Ms. Niluka Silva (Assigned to SE, NETF, CSB) |

---

## 7. Main Features

- **Role-Based Authentication**:
  - Secure login screen with credential verification.
  - Role-scoped navigation dashboard (Admin vs Lecturer).
  - Clean session termination and state reset upon logout.
- **Course & Subject Management (Admin)**:
  - Add, update, and remove courses.
  - Add and delete subjects linked to courses with cascade deletion.
- **Student Management (Admin)**:
  - Enroll students into specific courses.
  - Validation checks against duplicate registration numbers, invalid email formats, and invalid phone numbers.
- **Lecturer Management (Admin)**:
  - Register lecturer profiles linked to system user logins.
  - Assign and unassign subjects to lecturers.
- **Class Scheduling**:
  - Admin: Schedule, edit, and remove class sessions with conflict prevention (avoids lecturer double-booking for the same date and time slot).
  - Lecturer: View personal assigned teaching timetable.
- **Attendance Marking (Lecturer Only)**:
  - Lecturers select assigned sessions to view enrolled students.
  - Mark attendance status as **PRESENT**, **ABSENT**, or **LATE**.
  - Dynamic status counters and batch save/update capabilities.
- **Attendance Reports (Admin & Lecturer)**:
  - Filterable by student, course, subject, and date range.
  - Automatic calculation of summary metrics: Total Records, Present %, Absent %, Late %.
  - Lecturers are scoped to their assigned classes.

---

## 8. Layered Architecture Overview

The system strictly follows standard layered design patterns, keeping database operations separated from the JavaFX presentation layer:

```
UI (FXML & CSS)  <-->  Controllers  <-->  Services  <-->  DAOs  <-->  MySQL Database
                           |                 |
                           +--- Model Objects +
```

- **`com.sams.model`**: Entity classes representing domain objects (`User`, `Course`, `Subject`, `Student`, `Lecturer`, `ClassSession`, `Attendance`, `AttendanceReportItem`).
- **`com.sams.dao`**: Data Access Objects performing SQL queries and updates exclusively using `PreparedStatement` to ensure security and SQL injection defense.
- **`com.sams.service`**: Encapsulates business logic, validation rules, role verification, and double-booking conflict detection.
- **`com.sams.controller`**: JavaFX controllers handling user events, input binding, table updates, and error alerts.
- **`com.sams.util`**: Shared utility classes including `DBConnection` (JDBC connection lifecycle) and `Session` (logged-in user context).
- **`src/main/resources`**: UI assets including FXML markup files (`/fxml/`) and CSS styling (`/css/style.css`).

---

## 9. Notes on External Libraries and Resources

- **JavaFX 21.0.2**: Provided via OpenJFX Maven dependencies (`javafx-controls`, `javafx-fxml`). Provides desktop UI widgets and FXML declarative layout binding.
- **MySQL Connector/J 8.3.0**: Official JDBC Type 4 driver for communicating with MySQL.
- **No Heavy Frameworks / ORMs**: The application intentionally uses pure JDBC (`PreparedStatement`, `ResultSet`, `DriverManager`) rather than Spring, Hibernate, or JPA, ensuring transparency, minimal memory footprint, and beginner-friendly maintainability for coursework evaluation.