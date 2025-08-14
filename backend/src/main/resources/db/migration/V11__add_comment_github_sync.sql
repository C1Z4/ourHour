-- 댓글 GitHub 동기화 컬럼 추가
ALTER TABLE tbl_comment 
ADD COLUMN github_id BIGINT NULL,
ADD COLUMN is_github_synced BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN last_synced_at TIMESTAMP NULL,
ADD COLUMN github_etag VARCHAR(255) NULL,
ADD COLUMN sync_status ENUM('NOT_SYNCED', 'SYNCING', 'SYNCED', 'SYNC_FAILED') NOT NULL DEFAULT 'NOT_SYNCED',
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 기본값 채우기 (기존 데이터용)
UPDATE tbl_comment SET sync_status = 'NOT_SYNCED' WHERE sync_status IS NULL;
UPDATE tbl_comment SET is_github_synced = false WHERE is_github_synced IS NULL;


