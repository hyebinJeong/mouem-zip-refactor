-- 외래 키 제약 해제
SET FOREIGN_KEY_CHECKS = 0;

-- DROP TABLE (역순으로 삭제)
DROP TABLE IF EXISTS seizures;
DROP TABLE IF EXISTS provisional_seizures;
DROP TABLE IF EXISTS auction;
DROP TABLE IF EXISTS provisional_registrations;
DROP TABLE IF EXISTS trust;
DROP TABLE IF EXISTS provisional_disposition;
DROP TABLE IF EXISTS jeonse_right;
DROP TABLE IF EXISTS mortgages;
DROP TABLE IF EXISTS contract;
DROP TABLE IF EXISTS register_pdf;
DROP TABLE IF EXISTS final_report;
DROP TABLE IF EXISTS check_list;
DROP TABLE IF EXISTS property_address;
DROP TABLE IF EXISTS register_analysis_result;
DROP TABLE IF EXISTS registry_record;
DROP TABLE IF EXISTS special_contracts;
DROP TABLE IF EXISTS term;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS users;

-- 외래 키 제약 복원
SET FOREIGN_KEY_CHECKS = 1;

-- 테이블 생성
CREATE TABLE users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(50),
                       email VARCHAR(255),
                       role VARCHAR(50),
                       state VARCHAR(20)
);

CREATE TABLE category (
                          category_id INT PRIMARY KEY AUTO_INCREMENT,
                          category_name VARCHAR(255) NOT NULL,
                          explain_description VARCHAR(255),
                          category_color VARCHAR(255)
);

CREATE TABLE term (
                      term_id INT PRIMARY KEY AUTO_INCREMENT,
                      term_name VARCHAR(255) NOT NULL,
                      term_define VARCHAR(255),
                      term_example VARCHAR(255),
                      term_caution VARCHAR(255),
                      category_id INT,
                      FOREIGN KEY (category_id) REFERENCES category(category_id)
);

CREATE TABLE special_contracts (
                                   special_contracts_id INT PRIMARY KEY AUTO_INCREMENT,
                                   category VARCHAR(50) NOT NULL,
                                   importance VARCHAR(10),
                                   importance_color CHAR(6),
                                   description TEXT
);

CREATE TABLE registry_record (
                                 register_id INT PRIMARY KEY AUTO_INCREMENT,
                                 owner_name VARCHAR(20),
                                 total_senior_claim_amount BIGINT
);

CREATE TABLE register_analysis_result (
                                          register_id INT PRIMARY KEY AUTO_INCREMENT,
                                          user_id INT,
                                          road_address VARCHAR(255),
                                          detail_address VARCHAR(255),
                                          registration_number VARCHAR(50),
                                          register_score INT,
                                          jeonse_ratio_score INT,
                                          jeonse_ratio INT,
                                          expected_selling_price INT,
                                          registeration_date DATE,
                                          deposit BIGINT,
                                          analysis_date DATE,
                                          FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE property_address (
                                  road_address VARCHAR(255),
                                  detail_address VARCHAR(255),
                                  legal_code VARCHAR(10),
                                  PRIMARY KEY (road_address, detail_address),
                                  FOREIGN KEY (road_address, detail_address) REFERENCES register_analysis_result(road_address, detail_address)
);

CREATE TABLE check_list (
                            register_id INT PRIMARY KEY AUTO_INCREMENT,
                            user_id INT,
                            check_list_number INT,
                            check_list_score INT,
                            check_list_item VARCHAR(255),
                            FOREIGN KEY (user_id) REFERENCES users(user_id),
                            FOREIGN KEY (register_id) REFERENCES registry_record(register_id)
);

CREATE TABLE final_report (
                              report_id INT PRIMARY KEY AUTO_INCREMENT,
                              register_id INT,
                              user_id INT,
                              final_report_score INT,
                              register_score INT,
                              jeonse_ratio_score INT,
                              check_list_score INT,
                              FOREIGN KEY (user_id) REFERENCES users(user_id),
                              FOREIGN KEY (register_id, register_score, jeonse_ratio_score)
                                  REFERENCES register_analysis_result(register_id, register_score, jeonse_ratio_score),
                              FOREIGN KEY (check_list_score) REFERENCES check_list(check_list_score)
);

CREATE TABLE register_pdf (
                              pdf_id INT PRIMARY KEY AUTO_INCREMENT,
                              user_id INT,
                              file_name VARCHAR(255),
                              file_url VARCHAR(255),
                              upload_date DATE,
                              FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE contract (
                          contract_id INT PRIMARY KEY AUTO_INCREMENT,
                          user_id INT,
                          lessor_name VARCHAR(20),
                          lessee_name VARCHAR(20),
                          address VARCHAR(255),
                          land_category VARCHAR(100),
                          land_area DECIMAL(10,2),
                          building_usage VARCHAR(100),
                          building_area DECIMAL(10,2),
                          leased_part VARCHAR(100),
                          leased_area DECIMAL(10,2),
                          deposit BIGINT,
                          down_payment BIGINT,
                          balance BIGINT,
                          lease_start DATE,
                          lease_end DATE,
                          FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE mortgages (
                           mortgages_id INT PRIMARY KEY AUTO_INCREMENT,
                           register_number INT,
                           registration_reason VARCHAR(100),
                           secured_amount BIGINT,
                           mortgagee_name VARCHAR(50),
                           FOREIGN KEY (register_number) REFERENCES registry_record(register_id)
);

CREATE TABLE jeonse_right (
                              jeonse_right_id INT PRIMARY KEY AUTO_INCREMENT,
                              register_number INT,
                              registration_reason VARCHAR(100),
                              leaseholder_name VARCHAR(50),
                              duration DATE,
                              FOREIGN KEY (register_number) REFERENCES registry_record(register_id)
);

CREATE TABLE provisional_disposition (
                                         disposition_id INT PRIMARY KEY AUTO_INCREMENT,
                                         register_number INT,
                                         registration_reason VARCHAR(100),
                                         creditor_name VARCHAR(50),
                                         FOREIGN KEY (register_number) REFERENCES registry_record(register_id)
);

CREATE TABLE trust (
                       trust_id INT PRIMARY KEY AUTO_INCREMENT,
                       register_number INT,
                       registration_reason VARCHAR(100),
                       trustee_name VARCHAR(50),
                       FOREIGN KEY (register_number) REFERENCES registry_record(register_id)
);

CREATE TABLE provisional_registrations (
                                           provisional_registrations_id INT PRIMARY KEY AUTO_INCREMENT,
                                           register_number INT,
                                           registration_reason VARCHAR(100),
                                           pre_owner_name VARCHAR(50),
                                           FOREIGN KEY (register_number) REFERENCES registry_record(register_id)
);

CREATE TABLE auction (
                         auction_id INT PRIMARY KEY AUTO_INCREMENT,
                         register_number INT,
                         registration_reason VARCHAR(100),
                         creditor_name VARCHAR(50),
                         FOREIGN KEY (register_number) REFERENCES registry_record(register_id)
);

CREATE TABLE provisional_seizures (
                                      provisional_seizures_id INT PRIMARY KEY AUTO_INCREMENT,
                                      register_number INT,
                                      registration_reason VARCHAR(100),
                                      claimed_amount BIGINT,
                                      creditor_name VARCHAR(50),
                                      FOREIGN KEY (register_number) REFERENCES registry_record(register_id)
);

CREATE TABLE seizures (
                          seizures_id INT PRIMARY KEY,
                          register_number INT,
                          registration_reason VARCHAR(100),
                          right_holder_name VARCHAR(50),
                          FOREIGN KEY (register_number) REFERENCES registry_record(register_id)
);
