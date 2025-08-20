-- GitHub 동기화 관련 테이블 업데이트

-- member_github_mapping 테이블 생성
CREATE TABLE IF NOT EXISTS tbl_user_github_mapping (
    user_id BIGINT PRIMARY KEY,
    github_username VARCHAR(255) NOT NULL,
    github_email VARCHAR(255),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    linked_at TIMESTAMP
);

-- github_token 테이블 생성 
CREATE TABLE IF NOT EXISTS tbl_github_token (
    user_id BIGINT PRIMARY KEY,
    access_token VARCHAR(255) NOT NULL,
    CONSTRAINT fk_github_token_user_id FOREIGN KEY (user_id) REFERENCES tbl_user(user_id) ON DELETE CASCADE
);

-- project_github_integration 테이블 생성 
CREATE TABLE IF NOT EXISTS tbl_project_github_integration (
  integration_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  github_repository VARCHAR(255) NOT NULL,
  github_access_token VARCHAR(255) NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  github_id BIGINT NULL,
  is_github_synced BOOLEAN NOT NULL DEFAULT FALSE,
  last_synced_at TIMESTAMP NULL,
  github_etag VARCHAR(255) NULL,
  sync_status ENUM('NOT_SYNCED', 'SYNCING', 'SYNCED', 'SYNC_FAILED') NOT NULL DEFAULT 'NOT_SYNCED',
  CONSTRAINT fk_project_github_integration_project_id FOREIGN KEY (project_id) REFERENCES tbl_project(project_id) ON DELETE CASCADE,
  CONSTRAINT fk_project_github_integration_member_id FOREIGN KEY (member_id) REFERENCES tbl_member(member_id) ON DELETE CASCADE
);

-- tbl_issue 테이블에 GitHub 동기화 관련 컬럼 추가
ALTER TABLE tbl_issue 
ADD COLUMN github_id BIGINT NULL,
ADD COLUMN is_github_synced BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN last_synced_at TIMESTAMP NULL,
ADD COLUMN github_etag VARCHAR(255) NULL,
ADD COLUMN sync_status ENUM('NOT_SYNCED', 'SYNCING', 'SYNCED', 'SYNC_FAILED') NOT NULL DEFAULT 'NOT_SYNCED',
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- tbl_milestone 테이블에 GitHub 동기화 관련 컬럼 추가
ALTER TABLE tbl_milestone 
ADD COLUMN github_id BIGINT NULL,
ADD COLUMN is_github_synced BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN last_synced_at TIMESTAMP NULL,
ADD COLUMN github_etag VARCHAR(255) NULL,
ADD COLUMN sync_status ENUM('NOT_SYNCED', 'SYNCING', 'SYNCED', 'SYNC_FAILED') NOT NULL DEFAULT 'NOT_SYNCED',
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- GitHub 동기화 상태 기본값 설정 (기존 데이터용)
UPDATE tbl_issue SET sync_status = 'NOT_SYNCED' WHERE sync_status IS NULL;
UPDATE tbl_milestone SET sync_status = 'NOT_SYNCED' WHERE sync_status IS NULL;

-- GitHub 동기화 여부 기본값 설정 (기존 데이터용)
UPDATE tbl_issue SET is_github_synced = false WHERE is_github_synced IS NULL;
UPDATE tbl_milestone SET is_github_synced = false WHERE is_github_synced IS NULL; 