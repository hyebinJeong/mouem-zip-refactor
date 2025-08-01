# 기존 테이블 삭제 밑에 주석 풀고 진행하시면 됩니다.
#  -- 외래 키 제약 해제
# SET FOREIGN_KEY_CHECKS = 0;
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

INSERT INTO special_clause (category, importance, importance_color, description) VALUES
                                                                                     ('계약사항', '높음', '#FF0000', '임차인은 임대인의 사전 동의 없이 구조 변경을 할 수 없습니다.'),
                                                                                     ('관리비', '중간', '#FFA500', '관리비는 임차인이 부담하며, 매월 5일 이전까지 납부해야 합니다.'),
                                                                                     ('청소', '낮음', '#00FF00', '계약 종료 시 실내 청소 후 반환해야 합니다.'),
                                                                                     ('계약사항', '높음', '#FF0000', '보증금 반환은 임차인이 이사 후 7일 이내에 처리됩니다.'),
                                                                                     ('소음', '중간', '#FFA500', '밤 10시 이후 소음을 자제해 주세요.'),
                                                                                     ('반려동물', '높음', '#FF0000', '반려동물은 사전 협의 후 허용됩니다.'),
                                                                                     ('금연', '낮음', '#00FF00', '실내 금연입니다. 흡연 시 벌금이 부과될 수 있습니다.'),
                                                                                     ('수리', '중간', '#FFA500', '소모품 수리는 임차인이, 주요 설비는 임대인이 부담합니다.'),
                                                                                     ('정원관리', '낮음', '#00FF00', '정원 관리는 임차인이 책임집니다.'),
                                                                                     ('보안', '높음', '#FF0000', '출입문은 항상 잠금 상태를 유지해야 합니다.'),
                                                                                     ('주차', '중간', '#FFA500', '지정된 공간 외 주차 금지입니다.'),
                                                                                     ('기타', '낮음', '#00FF00', '기타 협의 사항은 별도 서면으로 작성됩니다.'),
                                                                                     ('공과금', '높음', '#FF0000', '전기, 수도 요금은 임차인이 부담합니다.'),
                                                                                     ('계약기간', '중간', '#FFA500', '계약기간 내 퇴거 시 위약금이 발생할 수 있습니다.'),
                                                                                     ('시설물', '높음', '#FF0000', '집기 파손 시 실비 청구됩니다.'),
                                                                                     ('청소', '낮음', '#00FF00', '퇴거 전 청소는 필수입니다.'),
                                                                                     ('중개수수료', '중간', '#FFA500', '중개수수료는 법정 기준에 따릅니다.'),
                                                                                     ('열쇠', '높음', '#FF0000', '열쇠 분실 시 교체 비용을 청구합니다.'),
                                                                                     ('인터넷', '중간', '#FFA500', '인터넷 설치는 임차인이 직접 해야 합니다.'),
                                                                                     ('입주', '낮음', '#00FF00', '입주는 계약일 이후 가능합니다.'),
                                                                                     ('보일러', '중간', '#FFA500', '보일러 고장은 즉시 신고해야 합니다.'),
                                                                                     ('환기', '낮음', '#00FF00', '주기적인 환기를 권장합니다.'),
                                                                                     ('청구', '중간', '#FFA500', '기타 비용 발생 시 별도 청구합니다.'),
                                                                                     ('변경사항', '높음', '#FF0000', '계약 변경 시 반드시 서면으로 처리해야 합니다.'),
                                                                                     ('책임', '높음', '#FF0000', '임차인은 재산 손해에 대한 책임이 있습니다.'),
                                                                                     ('입주조건', '중간', '#FFA500', '특수한 조건이 있을 경우 사전 합의합니다.'),
                                                                                     ('도배', '낮음', '#00FF00', '퇴거 시 도배 상태 유지 바랍니다.'),
                                                                                     ('해지', '높음', '#FF0000', '계약 해지 시 서면 통보가 필요합니다.'),
                                                                                     ('방문', '중간', '#FFA500', '임대인은 사전 통보 후 방문할 수 있습니다.'),
                                                                                     ('비상', '높음', '#FF0000', '비상 시 대피 경로는 입주자 안내문 참고 바랍니다.');