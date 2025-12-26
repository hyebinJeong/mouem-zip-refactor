/*MySQL Workbench에서 실행하기*/
DROP TABLE IF EXISTS final_report;
DROP TABLE IF EXISTS checklist;
DROP TABLE IF EXISTS jeonse_analysis;
DROP TABLE IF EXISTS registry_analysis;

DROP TEMPORARY TABLE IF EXISTS tmp_registry_ids;
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
                                   status BOOLEAN NOT NULL DEFAULT TRUE,
                                   file_name VARCHAR(100) NOT NULL,
                                   total_prior_amount BIGINT NOT NULL,
                                   FOREIGN KEY (user_id) REFERENCES users(user_id)
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
                                 FOREIGN KEY (registry_id) REFERENCES registry_analysis(registry_id),
                                 FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ============================================
-- 4. 체크리스트
-- ============================================
CREATE TABLE checklist (
                           registry_id INT PRIMARY KEY,
                           user_id INT,
                           checked TEXT NOT NULL,               -- 체크 여부 전체 JSON으로 저장
                           checklist_rating ENUM('판단보류', '안전', '보통', '주의', '위험') NOT NULL,
                           FOREIGN KEY (user_id) REFERENCES users(user_id),
                           FOREIGN KEY (registry_id) REFERENCES registry_analysis(registry_id)
);

-- ============================================
-- 5. 최종 리포트 및 요약
-- ============================================
CREATE TABLE final_report (
                              report_id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT,
                              registry_id INT,
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              status BOOLEAN NOT NULL DEFAULT TRUE,
                              FOREIGN KEY (user_id) REFERENCES users(user_id),
                              FOREIGN KEY (registry_id) REFERENCES registry_analysis(registry_id),
                              CONSTRAINT uq_user_registry UNIQUE (user_id, registry_id) -- user_id와 registry_id 조합의 중복 저장 방지
);


-- ============================================
-- 더미 데이터 삽입
-- ============================================

-- 1) registry_analysis 1000개 생성
INSERT INTO registry_analysis (
    user_id, address, risks, registry_name, registry_rating,
    status, file_name, total_prior_amount
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 1000
)
SELECT
    1,
    CONCAT('서울시 강남구 역삼동 ', n),
    '{
       "mortgageInfos":[],
       "seizureInfos":[],
       "auctionInfos":[],
       "provisionalSeizureInfos":[],
       "provisionalRegistrationInfos":[],
       "injunctionInfos":[],
       "jeonseRightInfos":[],
       "trustInfos":[]
     }',
    CONCAT('등기부등본_', n),
    '안전',
    TRUE,
    CONCAT('file_', n, '.pdf'),
    0
FROM seq;

-- 2) jeonse_analysis (모든 registry_id에 대해)
INSERT INTO jeonse_analysis (
    registry_id, user_id, expected_selling_price, deposit,
    jeonse_ratio, region_avg_jeonse_ratio, jeonse_ratio_rating
)
SELECT
    r.registry_id,
    1,
    FLOOR(100000 + (RAND() * 100000)),
    FLOOR(50000 + (RAND() * 50000)),
    ROUND((50000 + (RAND() * 50000)) / (100000 + (RAND() * 100000)) * 100, 2),
    FLOOR(60 + (RAND() * 20)),
    ELT(FLOOR(1 + (RAND() * 5)), '판단보류','안전','보통','주의','위험')
FROM registry_analysis r
WHERE r.user_id = 1;

-- 3) checklist (앞 절반)
INSERT INTO checklist (
    registry_id, user_id, checked, checklist_rating
)
SELECT
    t.registry_id,
    1,
    '[true,false,true,false,true,false,true,false,true]',
    ELT(FLOOR(1 + (RAND() * 5)), '판단보류','안전','보통','주의','위험')
FROM (
         SELECT registry_id,
                ROW_NUMBER() OVER (ORDER BY registry_id) AS rn,
                 COUNT(*) OVER () AS total_count
         FROM registry_analysis
         WHERE user_id = 1
     ) t
WHERE rn <= total_count / 2;

-- 4) final_report (앞 절반과 동일하게)
INSERT INTO final_report (
    user_id, registry_id, created_at, status
)
SELECT
    1,
    t.registry_id,
    TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY)),
    TRUE
FROM (
    SELECT registry_id,
    ROW_NUMBER() OVER (ORDER BY registry_id) AS rn,
    COUNT(*) OVER () AS total_count
    FROM registry_analysis
    WHERE user_id = 1
    ) t
WHERE rn <= total_count / 2;

-- 5) contract
INSERT INTO contract (
    user_id, contract_name, lessor_name, lessee_name, address,
    land_category, land_area, building_usage, building_area,
    leased_part, leased_area, deposit, down_payment, balance,
    maintenance_cost, lease_start, lease_end, special_clauses
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 500
)
SELECT
    1,  -- user_id
    CONCAT('계약_', n),
    CONCAT('임대인_', n),
    CONCAT('임차인_', n),
    CONCAT('서울시 강남구 테헤란로 ', n),
    '대지',                       -- land_category
    FLOOR(50 + (RAND() * 950)),   -- land_area
    '근린생활시설',               -- building_usage
    FLOOR(50 + (RAND() * 950)),   -- building_area
    CONCAT(n, '동 ', n, '호'),    -- leased_part
    FLOOR(30 + (RAND() * 70)),    -- leased_area
    FLOOR(1000 + (RAND() * 9000)),-- deposit
    FLOOR(500 + (RAND() * 500)),  -- down_payment
    FLOOR(500 + (RAND() * 500)),  -- balance
    FLOOR(10 + (RAND() * 40)),    -- maintenance_cost
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 365) DAY),
    DATE_ADD(CURDATE(), INTERVAL FLOOR(RAND() * 365) DAY),
    JSON_ARRAY(
            '계약 만료일 6개월 전~1개월 전까지 갱신 거절 통보 없으면 자동 연장됨',
            '임대인은 계약 갱신 거절 시 최소 2개월 전 통보해야 함'
    )
FROM seq;