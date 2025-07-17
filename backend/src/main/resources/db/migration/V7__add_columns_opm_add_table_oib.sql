-- =========================================================
-- 1) org_participant_member 확장
-- =========================================================
ALTER TABLE `tbl_org_participant_member`
    ADD COLUMN `status` ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE' AFTER `role`,
  ADD COLUMN `joined_at` DATETIME(6) NULL AFTER `status`,
  ADD COLUMN `left_at` DATETIME(6) NULL AFTER `joined_at`,
  ADD COLUMN `left_by_member_id` BIGINT NULL AFTER `left_at`,
  ADD CONSTRAINT `FK_opm_left_by_member`
    FOREIGN KEY (`left_by_member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE SET NULL,
  ADD INDEX `IDX_opm_org_status` (`org_id`, `status`);
;

-- =========================================================
-- 2) 초대 배치 테이블 생성
-- =========================================================
CREATE TABLE `tbl_org_invitation_batch` (
                                            `batch_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            `org_id` BIGINT NOT NULL,
                                            `inviter_member_id` BIGINT NOT NULL,
                                            `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                            CONSTRAINT `FK_batch_org`
                                                FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE,
                                            CONSTRAINT `FK_batch_inviter_member`
                                                FOREIGN KEY (`inviter_member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE
)
;

-- =========================================================
-- 3) 초대 테이블 ENUM 축소 준비 (CANCELLED → EXPIRED)
--    기존 데이터 중 CANCELLED 값이 있다면 EXPIRED로 대체
-- =========================================================
UPDATE `tbl_org_invitations`
SET `status` = 'EXPIRED'
WHERE `status` = 'CANCELLED';
;

-- =========================================================
-- 4) 초대 테이블 구조 변경
-- =========================================================
ALTER TABLE `tbl_org_invitations`
DROP INDEX `invited_email`,
DROP INDEX `invited_code`,
DROP COLUMN `invited_code`,
ADD COLUMN `batch_id` BIGINT NULL AFTER `inviter_member_id`,
ADD COLUMN `role` ENUM('ROOT_ADMIN','ADMIN', 'MEMBER', 'GUEST') NOT NULL DEFAULT 'MEMBER' AFTER `invited_email`,
MODIFY COLUMN `invited_email` VARCHAR(100) NOT NULL,
MODIFY COLUMN `status` ENUM('PENDING','ACCEPTED','EXPIRED') NOT NULL DEFAULT 'PENDING',
ADD CONSTRAINT `FK_invitation_batch` FOREIGN KEY (`batch_id`) REFERENCES `tbl_org_invitation_batch`(`batch_id`) ON DELETE SET NULL;
;

-- =========================================================
-- 5) role ENUM에 'GUEST' 추가
-- =========================================================
ALTER TABLE `tbl_org_participant_member`
MODIFY COLUMN `role` ENUM('ROOT_ADMIN','ADMIN','MEMBER','GUEST') NOT NULL DEFAULT 'MEMBER';
;
INSERT INTO `tbl_org_participant_member` (
    `org_id`, `member_id`, `dept_id`, `position_id`, `role`
) VALUES (
    1, 5, null, null, 'ROOT_ADMIN'
)