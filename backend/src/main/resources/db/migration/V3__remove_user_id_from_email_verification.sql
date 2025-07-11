-- 1. 외래키 삭제
ALTER TABLE `tbl_email_verification`
    DROP FOREIGN KEY `FK_user_TO_email_verification`;

-- 2. user_id 컬럼 삭제
ALTER TABLE `tbl_email_verification`
    DROP COLUMN `user_id`;

-- 3. email 컬럼 추가 (NOT NULL 안 붙임, 일단 NULL 허용)
ALTER TABLE `tbl_email_verification`
    ADD COLUMN `email` VARCHAR(255);

-- 4. 기존 테스트 데이터에 이메일 임시값 삽입
-- 실제 운영 DB면 여기서 JOIN으로 이메일 채워야 함
UPDATE `tbl_email_verification` SET `email` = 'user3@example.com' WHERE `verified_email_id` IN (1, 2, 3);

-- 5. email 컬럼 NOT NULL 설정
ALTER TABLE `tbl_email_verification`
    MODIFY COLUMN `email` VARCHAR(255) NOT NULL;