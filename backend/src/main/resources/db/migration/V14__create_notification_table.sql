-- 알림 테이블 생성
CREATE TABLE tbl_notification (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    message VARCHAR(500) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    related_id BIGINT,
    related_type VARCHAR(50),
    action_url VARCHAR(255),
    
    CONSTRAINT fk_notification_user 
        FOREIGN KEY (user_id) REFERENCES tbl_user(user_id) 
        ON DELETE CASCADE,
    
    INDEX idx_notification_user_created (user_id, created_at DESC),
    INDEX idx_notification_user_read (user_id, is_read),
    INDEX idx_notification_type (type),
    INDEX idx_notification_related (related_type, related_id)
);

-- 알림 타입 enum 체크 제약 조건
ALTER TABLE tbl_notification 
ADD CONSTRAINT chk_notification_type 
CHECK (type IN (
    'PROJECT_INVITATION',
    'CHANNEL_INVITATION',
    'CHAT_MESSAGE', 
    'ISSUE_ASSIGNED',
    'ISSUE_COMMENT',
    'ISSUE_COMMENT_REPLY',
    'POST_COMMENT',
    'POST_COMMENT_REPLY'
));