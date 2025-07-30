# 기존 테이블 삭제 밑에 주석 풀고 진행하시면 됩니다.
#  -- 외래 키 제약 해제
#  SET FOREIGN_KEY_CHECKS = 0;
#
#  -- DROP TABLE (역순으로 삭제)
#  DROP TABLE IF EXISTS seizures;
#  DROP TABLE IF EXISTS provisional_seizures;
#  DROP TABLE IF EXISTS auction;
#  DROP TABLE IF EXISTS provisional_registrations;
#  DROP TABLE IF EXISTS trust;
#  DROP TABLE IF EXISTS provisional_disposition;
#  DROP TABLE IF EXISTS jeonse_right;
#  DROP TABLE IF EXISTS mortgages;
#  DROP TABLE IF EXISTS contract;
#  DROP TABLE IF EXISTS register_pdf;
#  DROP TABLE IF EXISTS final_report;
#  DROP TABLE IF EXISTS check_list;
#  DROP TABLE IF EXISTS property_address;
#  DROP TABLE IF EXISTS register_analysis_result;
#  DROP TABLE IF EXISTS registry_record;
#  DROP TABLE IF EXISTS special_contracts;
#  DROP TABLE IF EXISTS term;
#  DROP TABLE IF EXISTS category;
#  DROP TABLE IF EXISTS users;
#
#  -- 외래 키 제약 복원
#  SET FOREIGN_KEY_CHECKS = 1;

-- 수정 후 테이블

DROP TABLE IF EXISTS contract_special_clause;
DROP TABLE IF EXISTS final_report;
DROP TABLE IF EXISTS checklist;
DROP TABLE IF EXISTS jeonse_analysis;
DROP TABLE IF EXISTS term;
DROP TABLE IF EXISTS register_pdf;
DROP TABLE IF EXISTS contract;
DROP TABLE IF EXISTS special_clause;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS registry_analysis;
DROP TABLE IF EXISTS users;

-- ============================================
-- 1. 사용자 관련
-- ============================================
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       kakao_id VARCHAR(100) UNIQUE NOT NULL,
                       name VARCHAR(50) NOT NULL,
                       role VARCHAR(50) DEFAULT 'USER'
);

-- ============================================
-- 2. 등기부등본 분석 관련
-- ============================================
CREATE TABLE registry_analysis (
                                   registry_id INT AUTO_INCREMENT PRIMARY KEY,
                                   user_id INT,
                                   address VARCHAR(255) NOT NULL,             -- 주소
                                   risks TEXT NOT NULL,                  -- 위험 요소 전체를 JSON으로 저장
                                   registry_name VARCHAR(100) NOT NULL,  -- 등기부등본 이름
                                   registry_rating ENUM('판단보류', '안전', '보통', '주의', '위험') NOT NULL, -- 등기부등본 등급
                                   analysis_date DATETIME DEFAULT CURRENT_TIMESTAMP,               -- 분석일
                                   status BOOLEAN NOT NULL,
                                   file_name VARCHAR(100) NOT NULL,
                                   FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================
-- 3. 전세가율 분석
-- ============================================
CREATE TABLE jeonse_analysis (
                                 registry_id INT PRIMARY KEY,
                                 user_id INT,
                                 expected_selling_price INT NOT NULL,       -- 예상 매매가
                                 deposit INT NOT NULL,                      -- 보증금
                                 jeonse_ratio DECIMAL(5,2) NOT NULL,        -- 전세가율
                                 region_avg_jeonse_ratio DECIMAL(5,2) NOT NULL, -- 지역 평균 전세가율
                                 jeonse_ratio_rating ENUM('판단보류', '안전', '보통', '주의', '위험') NOT NULL,
                                 FOREIGN KEY (registry_id) REFERENCES registry_analysis(registry_id) ON DELETE CASCADE,
                                 FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================
-- 4. 체크리스트
-- ============================================
CREATE TABLE checklist (
                           registry_id INT PRIMARY KEY,
                           user_id INT,
                           checked TEXT NOT NULL,               -- 체크 여부 전체 JSON으로 저장
                           checklist_rating ENUM('판단보류', '안전', '보통', '주의', '위험') NOT NULL,
                           FOREIGN KEY (user_id) REFERENCES users(user_id)  ON DELETE CASCADE,
                           FOREIGN KEY (registry_id) REFERENCES registry_analysis(registry_id) ON DELETE CASCADE
);

-- ============================================
-- 5. 최종 리포트 및 요약
-- ============================================
CREATE TABLE final_report (
                              report_id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT,
                              registry_id INT,
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              status BOOLEAN NOT NULL,
                              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                              FOREIGN KEY (registry_id) REFERENCES registry_analysis(registry_id) ON DELETE CASCADE
);

-- ============================================
-- 6. 계약서 작성 테이블
-- ============================================
CREATE TABLE contract (
                          contract_id INT AUTO_INCREMENT PRIMARY KEY, -- 계약서 번호 (PK)
                          user_id INT,                      -- 유저 ID (FK)
                          contract_name VARCHAR(100) NOT NULL,       -- 계약서 이름
                          lessor_name VARCHAR(20) NOT NULL,          -- 임대인 성명
                          lessee_name VARCHAR(20) NOT NULL,          -- 임차인 성명
                          address VARCHAR(255) NOT NULL,             -- 소재지
                          land_category VARCHAR(100) NOT NULL,       -- 토지 지목
                          land_area DECIMAL(10,2) NOT NULL,          -- 토지 면적
                          building_usage VARCHAR(100) NOT NULL,      -- 건물 구조/용도
                          building_area DECIMAL(10,2) NOT NULL,      -- 건물 면적
                          leased_part VARCHAR(100) NOT NULL,         -- 임차한 부분
                          leased_area DECIMAL(10,2) NOT NULL,        -- 임차한 면적
                          deposit BIGINT NOT NULL,                   -- 보증금
                          down_payment BIGINT NOT NULL,              -- 계약금
                          balance BIGINT NOT NULL,                   -- 잔금
                          maintenance_cost INT NOT NULL,             -- 관리비
                          lease_start DATE NOT NULL,                 -- 임대 시작일
                          lease_end DATE NOT NULL,                   -- 임대 종료일
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,                  -- 생성일
                          FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================
-- 7. 특약
-- ============================================
CREATE TABLE special_clause (
                                special_clause_id INT PRIMARY KEY AUTO_INCREMENT,  -- 특약번호(pk)
                                category VARCHAR(50) NOT NULL,                     -- 특약분류
                                importance VARCHAR(10) NOT NULL,                   -- 중요도
                                importance_color VARCHAR(50) NOT NULL,              -- 중요도 색상 코드
                                description TEXT NOT NULL                          -- 특약 설명
);

-- ============================================
-- 8. 계약서 작성 특약
-- ============================================
CREATE TABLE contract_special_clause (
                                         contract_id INT,                  -- 계약서 번호
                                         special_clause_id INT,            -- 특약번호
                                         PRIMARY KEY (contract_id, special_clause_id),
                                         FOREIGN KEY (contract_id) REFERENCES contract(contract_id) ON DELETE CASCADE,
                                         FOREIGN KEY (special_clause_id) REFERENCES special_clause(special_clause_id) ON DELETE CASCADE
);

-- ============================================
-- 9. 카테고리
-- ============================================
CREATE TABLE category(
                         category_id INT PRIMARY KEY AUTO_INCREMENT,    -- 카테고리 번호(pk)
                         category_name VARCHAR(100) NOT NULL,           -- 카테고리명
                         description VARCHAR(255) NOT NULL,             -- 카테고리 설명
                         category_color VARCHAR(50) NOT NULL             -- 카테고리 색상
);

-- ============================================
-- 10. 용어
-- ============================================
CREATE TABLE term(
                     term_id INT PRIMARY KEY AUTO_INCREMENT,        -- 용어번호(pk)
                     category_id INT,                      -- 카테고리 번호(fk)
                     term_name VARCHAR(100) NOT NULL,               -- 용어명
                     term_define VARCHAR(255) NOT NULL,             -- 용어 정의
                     term_example VARCHAR(255) NOT NULL,            -- 용어예시
                     term_caution VARCHAR(255) NOT NULL,            -- 용어 주의
                     FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE
);

# -- ============================================
# -- 11. 등기부등본 PDF 저장
# -- ============================================
# CREATE TABLE register_pdf (
#                               pdf_id INT PRIMARY KEY AUTO_INCREMENT,        -- pdf번호
#                               user_id INT,                         -- 유저번호
#                               file_name VARCHAR(255) NOT NULL,              -- 파일이름
#                               file_url VARCHAR(255) NOT NULL,               -- 파일url
#                               upload_date DATE NOT NULL,                    -- 업로드 시간
#                               FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
# );

-- 테스트용 코드
# INSERT INTO users (kakao_id, name) VALUES ('test@example.com', '테스트 사용자');