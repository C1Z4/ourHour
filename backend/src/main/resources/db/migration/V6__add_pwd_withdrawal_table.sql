
-- 이메일 인증 테이블
DROP TABLE IF EXISTS `tbl_email_verification`;

CREATE TABLE `tbl_email_verification` (
                                          `token_id` BIGINT AUTO_INCREMENT NOT NULL,
                                          `token` VARCHAR(255) NOT NULL,
                                          `email` VARCHAR(255) NOT NULL,
                                          `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                          `expired_at` DATETIME(6) NOT NULL,
                                          `is_used` BOOLEAN NOT NULL DEFAULT FALSE,
                                          PRIMARY KEY (`token_id`)
);


-- 비밀번호 재설정 테이블
DROP TABLE IF EXISTS `tbl_password_reset_verification`;

CREATE TABLE `tbl_password_reset_verification` (
                                                   `token_id` BIGINT AUTO_INCREMENT NOT NULL,
                                                   `token` VARCHAR(255) NOT NULL,
                                                   `email` VARCHAR(255) NOT NULL,
                                                   `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                                   `expired_at` DATETIME(6) NOT NULL,
                                                   `is_used` BOOLEAN NOT NULL DEFAULT FALSE,
                                                   PRIMARY KEY (`token_id`)
);


-- 회원 탈퇴 인증 테이블
DROP TABLE IF EXISTS `tbl_withdrawal_verification`;

CREATE TABLE `tbl_withdrawal_verification` (
                                               `token_id` BIGINT AUTO_INCREMENT NOT NULL,
                                               `user_id` BIGINT NOT NULL,
                                               `token` VARCHAR(255) NOT NULL,
                                               `email` VARCHAR(255) NOT NULL,
                                               `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                               `expired_at` DATETIME(6) NOT NULL,
                                               `is_used` BOOLEAN NOT NULL DEFAULT FALSE,
                                               PRIMARY KEY (`token_id`),
                                               CONSTRAINT `FK_user_TO_withdrawal` FOREIGN KEY (`user_id`) REFERENCES `tbl_user`(`user_id`) ON DELETE CASCADE
);