-- Schema for employment_db
-- The database must be created manually before starting the app:
-- CREATE DATABASE IF NOT EXISTS employment_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS universities (
    university_name VARCHAR(100) PRIMARY KEY,
    university_type VARCHAR(20),
    province VARCHAR(50),
    city VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS employment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    university_name VARCHAR(100),
    graduation_year INT,
    major_name VARCHAR(100),
    degree VARCHAR(10),
    employment_rate DECIMAL(5,1),
    employment_type VARCHAR(50),
    industry VARCHAR(50),
    province VARCHAR(50),
    city VARCHAR(50),
    salary_min DECIMAL(10,0),
    salary_max DECIMAL(10,0),
    salary_avg DECIMAL(10,1),
    salary_true DECIMAL(10,0),
    achievement_rate DECIMAL(5,3),
    data_source VARCHAR(200),
    FOREIGN KEY (university_name) REFERENCES universities(university_name),
    INDEX idx_emp_university (university_name),
    INDEX idx_emp_degree (degree),
    INDEX idx_emp_type (employment_type),
    INDEX idx_emp_industry (industry),
    INDEX idx_emp_province (province),
    INDEX idx_emp_year (graduation_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
