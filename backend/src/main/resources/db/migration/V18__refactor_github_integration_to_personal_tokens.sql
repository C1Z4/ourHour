-- V18: GitHub 연동을 개인별 토큰 관리 방식으로 리팩토링

-- 1. 사용자별 GitHub 토큰 테이블 생성 (이미 존재하면 스킵)
CREATE TABLE IF NOT EXISTS tbl_user_github_token (
    user_id BIGINT PRIMARY KEY,
    github_access_token TEXT NOT NULL COMMENT '암호화된 GitHub 액세스 토큰',
    github_username VARCHAR(255) NOT NULL COMMENT 'GitHub 사용자명',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES tbl_user(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_github_username (github_username)
) COMMENT = '사용자별 GitHub 토큰 관리';

-- 2. 프로젝트 GitHub 연동 테이블에서 개인 토큰 필드 제거 및 기본 토큰 사용자 추가
-- github_access_token 컬럼이 존재하면 제거 (안전 처리)
SET @sql = (
    SELECT CASE 
        WHEN COUNT(*) > 0 THEN 'ALTER TABLE tbl_project_github_integration DROP COLUMN github_access_token;'
        ELSE 'SELECT 1;'
    END
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'tbl_project_github_integration' 
    AND COLUMN_NAME = 'github_access_token'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- default_token_user_id 컬럼이 없으면 추가 (안전 처리)
SET @sql = (
    SELECT CASE 
        WHEN COUNT(*) = 0 THEN 'ALTER TABLE tbl_project_github_integration ADD COLUMN default_token_user_id BIGINT COMMENT \'기본 토큰 사용자 (백업용)\', ADD FOREIGN KEY (default_token_user_id) REFERENCES tbl_user(user_id) ON DELETE SET NULL;'
        ELSE 'SELECT 1;'
    END
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'tbl_project_github_integration' 
    AND COLUMN_NAME = 'default_token_user_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 기존 데이터 마이그레이션을 위한 임시 저장 (안전 처리)
-- 기존 GitHub 토큰 데이터를 tbl_github_token에서 새 테이블로 마이그레이션 (테이블이 존재하는 경우만)
INSERT IGNORE INTO tbl_user_github_token (user_id, github_access_token, github_username, created_at, updated_at)
SELECT 
    gt.user_id,
    gt.access_token as github_access_token,
    COALESCE(ugm.github_username, CONCAT('user_', gt.user_id)) as github_username,
    NOW(),
    NOW()
FROM tbl_github_token gt
LEFT JOIN tbl_user_github_mapping ugm ON gt.user_id = ugm.user_id
WHERE EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.TABLES 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'tbl_github_token'
);

-- 4. 프로젝트 연동의 기본 토큰 사용자 설정 (연동을 생성한 멤버의 사용자로 설정)
-- default_token_user_id 컬럼이 존재하는 경우만 업데이트
UPDATE tbl_project_github_integration pgi
JOIN tbl_member m ON pgi.member_id = m.member_id
SET pgi.default_token_user_id = m.user_id
WHERE pgi.default_token_user_id IS NULL
AND EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'tbl_project_github_integration' 
    AND COLUMN_NAME = 'default_token_user_id'
);