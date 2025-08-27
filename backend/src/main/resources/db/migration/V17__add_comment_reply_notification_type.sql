-- 알림 타입 체크 제약 조건에 COMMENT_REPLY 추가
ALTER TABLE tbl_notification 
DROP CONSTRAINT chk_notification_type;

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
    'POST_COMMENT_REPLY',
    'COMMENT_REPLY'
));