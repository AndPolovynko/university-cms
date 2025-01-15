CREATE TABLE IF NOT EXISTS roles (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS courses (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(300) NOT NULL
);

CREATE TABLE IF NOT EXISTS groups (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(50) PRIMARY KEY,
    login VARCHAR(50) NOT NULL,
    password VARCHAR(70) NOT NULL,
    email VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_details (
    user_type VARCHAR(25) NOT NULL,
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    group_id VARCHAR(50),
    job_title VARCHAR(50),
    FOREIGN KEY (group_id) REFERENCES groups(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS users_roles (
    user_id VARCHAR(50) NOT NULL,
    role_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS university_class_types (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS university_class_venues (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS university_classes (
    id VARCHAR(50) PRIMARY KEY,
    type_id VARCHAR(50),
    venue_id VARCHAR(50),
    course_id VARCHAR(50) NOT NULL,
    date_and_time TIMESTAMPTZ NOT NULL,
    FOREIGN KEY (type_id) REFERENCES university_class_types(id) ON DELETE SET NULL,
    FOREIGN KEY (venue_id) REFERENCES university_class_venues(id) ON DELETE SET NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE    
);

CREATE TABLE IF NOT EXISTS university_classes_groups (
    university_class_id VARCHAR(50) NOT NULL,
    group_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (university_class_id) REFERENCES university_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS university_classes_users (
    university_class_id VARCHAR(50) NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (university_class_id) REFERENCES university_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);