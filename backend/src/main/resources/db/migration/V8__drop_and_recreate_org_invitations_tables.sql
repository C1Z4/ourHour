ALTER TABLE `tbl_org_participant_member` MODIFY COLUMN `joined_at` DATE;
ALTER TABLE `tbl_org_participant_member` MODIFY COLUMN `left_at` DATE;


DROP TABLE IF EXISTS `tbl_org_invitations`;
DROP TABLE IF EXISTS `tbl_org_invitation_batch`;

-- ---------------------------------------------------------------
-- tbl_org_invitation_batch, tbl_org_invitations 새로 생성
-- ---------------------------------------------------------------

CREATE TABLE `tbl_org_invitation_batch` (
                                            `batch_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            `org_id` BIGINT NOT NULL,
                                            `inviter_member_id` BIGINT NOT NULL,
                                            `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                            CONSTRAINT `FK_batch_inviter_participant`
                                                FOREIGN KEY (`org_id`, `inviter_member_id`)
                                                    REFERENCES `tbl_org_participant_member`(`org_id`, `member_id`)
                                                    ON DELETE CASCADE
                                                    ON UPDATE CASCADE
);

-- ---------------------------------------------------------------

CREATE TABLE `tbl_org_invitations` (
                                       `invitation_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       `batch_id` BIGINT NOT NULL,
                                       `accepted_user_id` BIGINT NULL,
                                       `token` VARCHAR(64) NOT NULL UNIQUE,
                                       `email` VARCHAR(255) NULL,
                                       `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                       `expired_at` DATETIME(6) NULL,
                                       `is_used` BOOLEAN NOT NULL DEFAULT FALSE,
                                       `role` ENUM('ROOT_ADMIN','ADMIN','MEMBER', 'GUEST') NOT NULL DEFAULT 'MEMBER',
                                       `status` ENUM('PENDING', 'ACCEPTED','EXPIRED') NOT NULL DEFAULT 'PENDING',
                                       `accepted_at` DATETIME(6) NULL,
                                       CONSTRAINT `FK_invitation_batch`
                                           FOREIGN KEY (`batch_id`)
                                               REFERENCES `tbl_org_invitation_batch`(`batch_id`)
                                               ON DELETE CASCADE,
                                       CONSTRAINT `FK_invitation_accept_user`
                                           FOREIGN KEY (`accepted_user_id`)
                                               REFERENCES `tbl_user`(`user_id`)
                                               ON DELETE SET NULL
);
