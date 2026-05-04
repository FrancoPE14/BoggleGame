CREATE DATABASE IF NOT EXISTS boggle;
USE boggle;

CREATE TABLE boggle_user (
    user_name VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    matches_won INT DEFAULT 0,
    highest_score INT DEFAULT 0,
    profile_picture LONGTEXT DEFAULT NULL,
    PRIMARY KEY (user_name)
);