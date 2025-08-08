-- CREATE DATABASE ourHour_db; -- Flyway 사용 시 보통 생략
-- USE ourHour_db; -- Flyway 사용 시 보통 생략

-- 기존 테이블 DROP 순서는 외래 키 제약 조건을 고려하여 유지
DROP TABLE IF EXISTS `tbl_comment_like`;
DROP TABLE IF EXISTS `tbl_post_like`;
DROP TABLE IF EXISTS `tbl_post_bookmark`;
DROP TABLE IF EXISTS `tbl_comment`;
DROP TABLE IF EXISTS `tbl_post`;
DROP TABLE IF EXISTS `tbl_board_fix`; -- 새로 추가된 테이블의 DROP 문 추가
DROP TABLE IF EXISTS `tbl_board`;
DROP TABLE IF EXISTS `tbl_issue`;
DROP TABLE IF EXISTS `tbl_milestone`;
DROP TABLE IF EXISTS `tbl_issue_tag`;
DROP TABLE IF EXISTS `tbl_project_participant_department`;
DROP TABLE IF EXISTS `tbl_project_participant`;
DROP TABLE IF EXISTS `tbl_project`;
DROP TABLE IF EXISTS `tbl_chat_participant`;
DROP TABLE IF EXISTS `tbl_chat_message`;
DROP TABLE IF EXISTS `tbl_chat_room`;
DROP TABLE IF EXISTS `tbl_email_recipient`;
DROP TABLE IF EXISTS `tbl_mail`;
DROP TABLE IF EXISTS `tbl_refresh_token`;
DROP TABLE IF EXISTS `tbl_org_participant_member`;
DROP TABLE IF EXISTS `tbl_org_invitations`; -- 새로 추가된 테이블의 DROP 문 추가
DROP TABLE IF EXISTS `tbl_email_verification`; -- 새로 추가된 테이블의 DROP 문 추가
DROP TABLE IF EXISTS `tbl_member`;
DROP TABLE IF EXISTS `tbl_position`;
DROP TABLE IF EXISTS `tbl_department`;
DROP TABLE IF EXISTS `tbl_org`;
DROP TABLE IF EXISTS `tbl_user`;


CREATE TABLE `tbl_org` (
                           `org_id` BIGINT AUTO_INCREMENT NOT NULL,
                           `name` VARCHAR(100) NOT NULL,
                           `address` VARCHAR(255) NULL,
                           `email` VARCHAR(100) NULL,
                           `representative_name` VARCHAR(50) NULL,
                           `phone` VARCHAR(20) NULL,
                           `business_number` VARCHAR(20) NULL,
                           `logo_img_url` VARCHAR(512) NULL,
                           PRIMARY KEY (`org_id`)
);

CREATE TABLE `tbl_user` (
                            `user_id` BIGINT AUTO_INCREMENT NOT NULL,
                            `email` VARCHAR(100) NOT NULL UNIQUE,
                            `password` VARCHAR(255) NOT NULL,
                            `platform` ENUM('OURHOUR', 'GITHUB', 'GOOGLE', 'KAKAO') NOT NULL,
                            `is_email_verified` BOOLEAN NOT NULL DEFAULT FALSE, -- ⭐ 추가/수정
                            `email_verified_at` DATETIME(6) NULL, -- ⭐ 추가/수정
                            PRIMARY KEY (`user_id`)
);

CREATE TABLE `tbl_department` (
                                  `dept_id` BIGINT AUTO_INCREMENT NOT NULL,
                                  `org_id` BIGINT NOT NULL,
                                  `name` VARCHAR(100) NULL,
                                  PRIMARY KEY (`dept_id`),
                                  CONSTRAINT `FK_org_TO_department` FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_position` (
                                `position_id` BIGINT AUTO_INCREMENT NOT NULL,
                                `org_id` BIGINT NOT NULL,
                                `name` VARCHAR(100) NULL,
                                PRIMARY KEY (`position_id`),
                                CONSTRAINT `FK_org_TO_position` FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_member` (
                              `member_id` BIGINT AUTO_INCREMENT NOT NULL,
                              `user_id` BIGINT NOT NULL,
                              `name` VARCHAR(50) NOT NULL,
                              `phone` VARCHAR(20) NULL,
                              `email` VARCHAR(100) NOT NULL,
                              `profile_img_url` VARCHAR(512) NULL,
                              PRIMARY KEY (`member_id`),
                              CONSTRAINT `FK_user_TO_member` FOREIGN KEY (`user_id`) REFERENCES `tbl_user`(`user_id`) ON DELETE CASCADE
);

-- ⭐ 새로 추가된 테이블: 이메일 인증
CREATE TABLE `tbl_email_verification` (
                                          `verified_email_id` BIGINT AUTO_INCREMENT NOT NULL,
                                          `user_id` BIGINT NOT NULL,
                                          `token` VARCHAR(255) NOT NULL,
                                          `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                          `expired_at` DATETIME(6) NOT NULL,
                                          `is_used` BOOLEAN NOT NULL DEFAULT FALSE,
                                          PRIMARY KEY (`verified_email_id`),
                                          CONSTRAINT `FK_user_TO_email_verification` FOREIGN KEY (`user_id`) REFERENCES `tbl_user`(`user_id`) ON DELETE CASCADE
);

-- ⭐ 새로 추가된 테이블: 조직 초대
CREATE TABLE `tbl_org_invitations` (
                                       `invitation_id` BIGINT AUTO_INCREMENT NOT NULL,
                                       `org_id` BIGINT NOT NULL,
                                       `inviter_member_id` BIGINT NOT NULL,
                                       `accepted_user_id` BIGINT NULL, -- 초대를 수락한 사용자의 ID (선택 사항)
                                       `invited_email` VARCHAR(100) NOT NULL UNIQUE, -- 초대받은 이메일 (고유해야 함)
                                       `invited_code` VARCHAR(255) NOT NULL UNIQUE, -- 초대 코드 (고유해야 함)
                                       `status` ENUM('PENDING', 'ACCEPTED', 'EXPIRED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
                                       `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                       `expires_at` DATETIME(6) NOT NULL,
                                       `accepted_at` DATETIME(6) NULL,
                                       PRIMARY KEY (`invitation_id`),
                                       CONSTRAINT `FK_org_TO_org_invitations` FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE,
                                       CONSTRAINT `FK_inviter_member_TO_org_invitations` FOREIGN KEY (`inviter_member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE,
                                       CONSTRAINT `FK_accepted_user_TO_org_invitations` FOREIGN KEY (`accepted_user_id`) REFERENCES `tbl_user`(`user_id`) ON DELETE SET NULL
);

CREATE TABLE `tbl_refresh_token` (
                                     `token_id` BIGINT AUTO_INCREMENT NOT NULL, -- ⭐ VARCHAR(255) -> BIGINT AUTO_INCREMENT NOT NULL
                                     `user_id` BIGINT NOT NULL,
                                     `token` VARCHAR(512) NOT NULL, -- ⭐ VARCHAR(255) -> VARCHAR(512) NOT NULL
                                     `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), -- ⭐ VARCHAR(255) -> DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                                     `expires_at` DATETIME(6) NOT NULL, -- ⭐ VARCHAR(255) -> DATETIME(6) NOT NULL
                                     PRIMARY KEY (`token_id`),
                                     CONSTRAINT `FK_user_TO_refresh_token` FOREIGN KEY (`user_id`) REFERENCES `tbl_user`(`user_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_org_participant_member` (
                                              `org_id` BIGINT NOT NULL,
                                              `member_id` BIGINT NOT NULL,
                                              `dept_id` BIGINT NULL, -- ⭐ NOT NULL -> NULL (ON DELETE SET NULL 때문)
                                              `position_id` BIGINT NULL, -- ⭐ NOT NULL -> NULL (ON DELETE SET NULL 때문)
                                              `role` ENUM('ROOT_ADMIN','ADMIN', 'MEMBER') NOT NULL DEFAULT 'MEMBER', -- ⭐ VARCHAR(255) -> ENUM
                                              PRIMARY KEY (`org_id`, `member_id`),
                                              CONSTRAINT `FK_org_TO_org_participant_member` FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE,
                                              CONSTRAINT `FK_member_TO_org_participant_member` FOREIGN KEY (`member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE,
                                              CONSTRAINT `FK_department_TO_org_participant_member` FOREIGN KEY (`dept_id`) REFERENCES `tbl_department`(`dept_id`) ON DELETE SET NULL,
                                              CONSTRAINT `FK_position_TO_org_participant_member` FOREIGN KEY (`position_id`) REFERENCES `tbl_position`(`position_id`) ON DELETE SET NULL
);

CREATE TABLE `tbl_project` (
                               `project_id` BIGINT AUTO_INCREMENT NOT NULL,
                               `org_id` BIGINT NOT NULL,
                               `name` VARCHAR(255) NOT NULL,
                               `description` TEXT NULL, -- ⭐ VARCHAR(255) -> TEXT
                               `start_at` DATETIME(6) NULL, -- ⭐ VARCHAR(255) -> DATETIME(6)
                               `end_at` DATETIME(6) NULL, -- ⭐ VARCHAR(255) -> DATETIME(6)
                               `status` ENUM('NOT_STARTED','PLANNING' ,'IN_PROGRESS', 'DONE', 'ARCHIVE') NOT NULL DEFAULT 'NOT_STARTED', -- ⭐ VARCHAR(255) -> ENUM
                               PRIMARY KEY (`project_id`),
                               CONSTRAINT `FK_org_TO_project` FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_project_participant` (
                                           `project_id` BIGINT NOT NULL,
    -- `org_id` BIGINT NOT NULL, -- ⭐ 이 컬럼은 기존 DDL에 없었으며, project_id를 통해 org_id를 알 수 있으므로 제거하는 것이 일반적입니다.
                                           `member_id` BIGINT NOT NULL,
                                           PRIMARY KEY (`project_id`, `member_id`), -- ⭐ org_id 제거에 따라 PK 수정
                                           CONSTRAINT `FK_project_TO_participant` FOREIGN KEY (`project_id`) REFERENCES `tbl_project`(`project_id`) ON DELETE CASCADE,
                                           CONSTRAINT `FK_member_TO_participant` FOREIGN KEY (`member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_project_participant_department` (
                                                      `project_id` BIGINT NOT NULL,
                                                      `dept_id` BIGINT NOT NULL,
                                                      PRIMARY KEY (`project_id`, `dept_id`),
                                                      CONSTRAINT `FK_project_TO_participant_dept` FOREIGN KEY (`project_id`) REFERENCES `tbl_project`(`project_id`) ON DELETE CASCADE,
                                                      CONSTRAINT `FK_department_TO_participant_dept` FOREIGN KEY (`dept_id`) REFERENCES `tbl_department`(`dept_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_milestone` (
                                 `milestone_id` BIGINT AUTO_INCREMENT NOT NULL,
                                 `project_id` BIGINT NOT NULL,
                                 `name` VARCHAR(255) NOT NULL,
                                 `progress` TINYINT UNSIGNED NOT NULL DEFAULT 0, -- ⭐ VARCHAR(255) -> TINYINT UNSIGNED
                                 PRIMARY KEY (`milestone_id`),
                                 CONSTRAINT `FK_project_TO_milestone` FOREIGN KEY (`project_id`) REFERENCES `tbl_project`(`project_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_issue_tag` (
                                 `issue_tag_id` BIGINT AUTO_INCREMENT NOT NULL,
                                 `name` VARCHAR(50) NOT NULL,
                                 `color` ENUM('PINK', 'YELLOW', 'GREEN', 'BLUE', 'PURPLE') NOT NULL DEFAULT 'PINK', -- ⭐ VARCHAR(255) -> ENUM
                                 PRIMARY KEY (`issue_tag_id`)
);

CREATE TABLE `tbl_issue` (
                             `issue_id` BIGINT AUTO_INCREMENT NOT NULL,
                             `milestone_id` BIGINT NOT NULL,
                             `issue_tag_id` BIGINT NULL, -- ⭐ NOT NULL -> NULL (ON DELETE SET NULL 때문)
                             `assignee_id` BIGINT NULL, -- ⭐ NOT NULL -> NULL (ON DELETE SET NULL 때문)
                             `name` VARCHAR(255) NOT NULL,
                             `content` TEXT NULL, -- ⭐ VARCHAR(255) -> TEXT
                             `status` ENUM('BACKLOG', 'NOT_STARTED', 'PENDING', 'IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'BACKLOG', -- ⭐ VARCHAR(255) -> ENUM
                             PRIMARY KEY (`issue_id`),
                             CONSTRAINT `FK_milestone_TO_issue` FOREIGN KEY (`milestone_id`) REFERENCES `tbl_milestone`(`milestone_id`) ON DELETE CASCADE,
                             CONSTRAINT `FK_issue_tag_TO_issue` FOREIGN KEY (`issue_tag_id`) REFERENCES `tbl_issue_tag`(`issue_tag_id`) ON DELETE SET NULL,
                             CONSTRAINT `FK_assignee_TO_issue` FOREIGN KEY (`assignee_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE SET NULL
);

CREATE TABLE `tbl_board` (
                             `board_id` BIGINT AUTO_INCREMENT NOT NULL,
                             `org_id` BIGINT NOT NULL,
                             `name` VARCHAR(100) NOT NULL,
                             `is_fixed` BOOLEAN NOT NULL DEFAULT FALSE, -- ⭐ VARCHAR(255) -> BOOLEAN
                             PRIMARY KEY (`board_id`),
                             CONSTRAINT `FK_org_TO_board` FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE
);

-- ⭐ 새로 추가된 테이블: 게시판 고정
CREATE TABLE `tbl_board_fix` (
                                 `board_id` BIGINT NOT NULL,
                                 `member_id` BIGINT NOT NULL,
                                 PRIMARY KEY (`board_id`, `member_id`),
                                 CONSTRAINT `FK_board_TO_board_fix` FOREIGN KEY (`board_id`) REFERENCES `tbl_board`(`board_id`) ON DELETE CASCADE,
                                 CONSTRAINT `FK_member_TO_board_fix` FOREIGN KEY (`member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_post` (
                            `post_id` BIGINT AUTO_INCREMENT NOT NULL,
                            `board_id` BIGINT NULL, -- ⭐ NOT NULL -> NULL
                            `author_id` BIGINT NULL, -- ⭐ NOT NULL -> NULL
                            `title` VARCHAR(255) NOT NULL,
                            `content` LONGTEXT NOT NULL, -- ⭐ VARCHAR(255) -> LONGTEXT NOT NULL
                            `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), -- ⭐ VARCHAR(255) -> DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                            PRIMARY KEY (`post_id`),
                            CONSTRAINT `FK_board_TO_post` FOREIGN KEY (`board_id`) REFERENCES `tbl_board`(`board_id`) ON DELETE CASCADE,
                            CONSTRAINT `FK_member_TO_post` FOREIGN KEY (`author_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE SET NULL
);

CREATE TABLE `tbl_post_like` (
                                    `post_id` BIGINT NOT NULL, -- ⭐ VARCHAR(255) -> BIGINT
                                    `member_id` BIGINT NOT NULL, -- ⭐ author_id -> member_id로 변경 및 타입 수정
                                    PRIMARY KEY (`post_id`, `member_id`),
                                    FOREIGN KEY (`post_id`) REFERENCES `tbl_post`(`post_id`) ON DELETE CASCADE,
                                    FOREIGN KEY (`member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_comment` (
                               `comment_id` BIGINT AUTO_INCREMENT NOT NULL, -- ⭐ VARCHAR(255) -> BIGINT AUTO_INCREMENT NOT NULL
                               `post_id` BIGINT NULL, -- ⭐ NOT NULL -> NULL
                               `issue_id` BIGINT NULL, -- ⭐ NOT NULL -> NULL
                               `author_id` BIGINT NOT NULL,
                               `parent_comment_id` BIGINT NULL, -- ⭐ VARCHAR(255) -> BIGINT
                               `content` TEXT NOT NULL, -- ⭐ VARCHAR(255) -> TEXT NOT NULL
                               `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), -- ⭐ VARCHAR(255) -> DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                               PRIMARY KEY (`comment_id`),
                               CONSTRAINT `FK_post_TO_comment` FOREIGN KEY (`post_id`) REFERENCES `tbl_post`(`post_id`) ON DELETE CASCADE,
                               CONSTRAINT `FK_issue_TO_comment` FOREIGN KEY (`issue_id`) REFERENCES `tbl_issue`(`issue_id`) ON DELETE CASCADE,
                               CONSTRAINT `FK_member_TO_comment` FOREIGN KEY (`author_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE,
                               CONSTRAINT `FK_parent_comment_TO_comment` FOREIGN KEY (`parent_comment_id`) REFERENCES `tbl_comment`(`comment_id`) ON DELETE CASCADE,
                               CONSTRAINT `check_comment_target` CHECK ((`post_id` IS NOT NULL AND `issue_id` IS NULL) OR (`post_id` IS NULL AND `issue_id` IS NOT NULL))
);

CREATE TABLE `tbl_comment_like` (
                                    `comment_id` BIGINT NOT NULL, -- ⭐ VARCHAR(255) -> BIGINT
                                    `member_id` BIGINT NOT NULL, -- ⭐ author_id -> member_id로 변경 및 타입 수정
                                    PRIMARY KEY (`comment_id`, `member_id`),
                                    FOREIGN KEY (`comment_id`) REFERENCES `tbl_comment`(`comment_id`) ON DELETE CASCADE,
                                    FOREIGN KEY (`member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_chat_room` (
                                 `room_id` BIGINT AUTO_INCREMENT NOT NULL,
                                 `name` VARCHAR(100) NULL,
                                 `color` ENUM('PINK', 'YELLOW', 'GREEN', 'BLUE', 'PURPLE') NOT NULL DEFAULT 'PINK', -- ⭐ VARCHAR(255) -> ENUM
                                 `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), -- ⭐ VARCHAR(255) -> DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                                 PRIMARY KEY (`room_id`)
);

CREATE TABLE `tbl_chat_participant` (
                                        `room_id` BIGINT NOT NULL,
                                        `member_id` BIGINT NOT NULL,
                                        PRIMARY KEY (`room_id`, `member_id`),
                                        CONSTRAINT `FK_chat_room_TO_chat_participant` FOREIGN KEY (`room_id`) REFERENCES `tbl_chat_room`(`room_id`) ON DELETE CASCADE,
                                        CONSTRAINT `FK_member_TO_chat_participant` FOREIGN KEY (`member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_chat_message` (
                                    `chat_message_id` BIGINT AUTO_INCREMENT NOT NULL,
                                    `room_id` BIGINT NOT NULL, -- ⭐ member_id -> room_id (순서 변경)
                                    `sender_id` BIGINT NOT NULL, -- ⭐ member_id -> sender_id로 변경
                                    `content` TEXT NOT NULL, -- ⭐ VARCHAR(255) -> TEXT NOT NULL
                                    `sent_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), -- ⭐ VARCHAR(255) -> DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                                    PRIMARY KEY (`chat_message_id`),
                                    CONSTRAINT `FK_chat_room_TO_chat_message` FOREIGN KEY (`room_id`) REFERENCES `tbl_chat_room`(`room_id`) ON DELETE CASCADE,
                                    CONSTRAINT `FK_member_TO_chat_message` FOREIGN KEY (`sender_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_mail` (
                            `mail_id` BIGINT AUTO_INCREMENT NOT NULL,
                            `member_id` BIGINT NOT NULL,
                            `title` VARCHAR(255) NOT NULL, -- ⭐ NULL -> NOT NULL
                            `content` TEXT NOT NULL, -- ⭐ VARCHAR(255) -> TEXT NOT NULL
                            `sent_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), -- ⭐ VARCHAR(255) -> DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                            `received_at` DATETIME(6) NULL, -- ⭐ VARCHAR(255) -> DATETIME(6)
                            `email_direction` ENUM('SENT', 'RECEIVED') NOT NULL, -- ⭐ VARCHAR(255) -> ENUM
                            PRIMARY KEY (`mail_id`),
                            CONSTRAINT `FK_member_TO_mail` FOREIGN KEY (`member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_email_recipient` (
                                       `mail_id` BIGINT NOT NULL, -- ⭐ VARCHAR(255) -> BIGINT
                                       `recipient_email` VARCHAR(255) NOT NULL, -- ⭐ sender_email -> recipient_email로 변경 및 NOT NULL
                                       PRIMARY KEY (`mail_id`, `recipient_email`), -- ⭐ 복합 키로 변경
                                       CONSTRAINT `FK_mail_TO_email_recipient` FOREIGN KEY (`mail_id`) REFERENCES `tbl_mail`(`mail_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_post_bookmark` (
                                     `post_id` BIGINT NOT NULL,
                                     `member_id` BIGINT NOT NULL,
                                     PRIMARY KEY (`post_id`, `member_id`),
                                     CONSTRAINT `FK_post_TO_post_bookmark` FOREIGN KEY (`post_id`) REFERENCES `tbl_post`(`post_id`) ON DELETE CASCADE,
                                     CONSTRAINT `FK_member_TO_post_bookmark` FOREIGN KEY (`member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE
);

-- 인덱스 추가 (기존 스크립트에 있었음)
CREATE INDEX `idx_post_author` ON `tbl_post`(`author_id`);
CREATE INDEX `idx_issue_assignee` ON `tbl_issue`(`assignee_id`);
CREATE INDEX `idx_chat_message_sent_at` ON `tbl_chat_message`(`sent_at`);

-- 1. tbl_user 데이터
INSERT INTO `tbl_user` (`user_id`, `email`, `password`, `platform`, `is_email_verified`, `email_verified_at`) VALUES
                                                                                                                  (1, 'user1@ourhour.dev', '$2a$10$abcdefghijklmnopqrstuv.abcdefghijklmnopqrstuv', 'OURHOUR', TRUE, '2024-01-10 10:00:00.000000'),
                                                                                                                  (2, 'user2@google.com', '$2a$10$abcdefghijklmnopqrstuv.abcdefghijklmnopqrstuv', 'GOOGLE', TRUE, '2024-02-15 11:30:00.000000'),
                                                                                                                  (3, 'user3@github.com', '$2a$10$abcdefghijklmnopqrstuv.abcdefghijklmnopqrstuv', 'GITHUB', FALSE, NULL), -- 이메일 미인증 사용자
                                                                                                                  (4, 'user4@kakao.com', '$2a$10$abcdefghijklmnopqrstuv.abcdefghijklmnopqrstuv', 'KAKAO', TRUE, '2024-03-20 14:00:00.000000'),
                                                                                                                  (5, 'user5@ourhour.dev', '$2a$10$abcdefghijklmnopqrstuv.abcdefghijklmnopqrstuv', 'OURHOUR', TRUE, '2024-04-01 09:00:00.000000');


-- 2. tbl_org 데이터
INSERT INTO `tbl_org` (`org_id`, `name`, `address`, `email`, `representative_name`, `phone`, `business_number`, `logo_img_url`) VALUES
                                                                                                                                    (1, '아워아워 주식회사', '서울시 강남구 테헤란로 123', 'contact@ourhour.com', '김대표', '02-1234-5678', '123-45-67890', 'https://example.com/logo_ourhour.png'),
                                                                                                                                    (2, '미래솔루션즈', '부산시 해운대구 센텀로 456', 'info@futuresol.com', '이대표', '051-9876-5432', '987-65-43210', 'https://example.com/logo_future.png'),
                                                                                                                                    (3, '새싹 스타트업', '경기도 성남시 분당구 판교로 789', 'contact@saessak.co', '박대표', '031-1111-2222', '111-22-33333', NULL), -- 로고 이미지 없음
                                                                                                                                    (4, '글로벌 파트너스', '제주특별자치도 제주시 첨단로 10', 'global@partners.com', '최대표', '064-5555-6666', '555-66-77777', 'https://example.com/logo_global.png');


-- 3. tbl_member 데이터
INSERT INTO `tbl_member` (`member_id`, `user_id`, `name`, `phone`, `email`, `profile_img_url`) VALUES
                                                                                                   (1, 1, '김아워', '010-1111-2222', 'user1@ourhour.dev', 'https://example.com/profile_kim.png'),
                                                                                                   (2, 2, '이솔루션', '010-3333-4444', 'user2@google.com', 'https://example.com/profile_lee.png'),
                                                                                                   (3, 3, '박새싹', '010-5555-6666', 'user3@github.com', NULL), -- 프로필 이미지 없음
                                                                                                   (4, 4, '최글로벌', '010-7777-8888', 'user4@kakao.com', 'https://example.com/profile_choi.png'),
                                                                                                   (5, 5, '정테스트', '010-9999-0000', 'user5@ourhour.dev', NULL);


-- 4. tbl_department 데이터
INSERT INTO `tbl_department` (`dept_id`, `org_id`, `name`) VALUES
                                                               (1, 1, '개발팀'),
                                                               (2, 1, '디자인팀'),
                                                               (3, 2, '영업팀'),
                                                               (4, 2, '마케팅팀'),
                                                               (5, 3, '경영지원팀');


-- 5. tbl_position 데이터
INSERT INTO `tbl_position` (`position_id`, `org_id`, `name`) VALUES
                                                                 (1, 1, '팀장'),
                                                                 (2, 1, '사원'),
                                                                 (3, 2, '부장'),
                                                                 (4, 2, '대리'),
                                                                 (5, 3, '대표');


-- 6. tbl_org_participant_member 데이터
-- 시나리오:
-- - org_id 1: member 1(ADMIN, 개발팀/팀장), member 3(MEMBER, 미소속)
-- - org_id 2: member 2(ADMIN, 영업팀/부장), member 4(MEMBER, 마케팅팀/대리)
-- - org_id 3: member 3(ADMIN, 경영지원팀/대표) - 한 멤버가 여러 조직에 속함
-- - org_id 4: member 5(ADMIN, 미소속)
INSERT INTO `tbl_org_participant_member` (`org_id`, `member_id`, `dept_id`, `position_id`, `role`) VALUES
                                                                                                       (1, 1, 1, 1, 'ADMIN'), -- 아워아워: 김아워(개발팀/팀장, ADMIN)
                                                                                                       (1, 3, NULL, NULL, 'MEMBER'), -- 아워아워: 박새싹(미소속, MEMBER)
                                                                                                       (2, 2, 3, 3, 'ADMIN'), -- 미래솔루션즈: 이솔루션(영업팀/부장, ADMIN)
                                                                                                       (2, 4, 4, 4, 'MEMBER'), -- 미래솔루션즈: 최글로벌(마케팅팀/대리, MEMBER)
                                                                                                       (3, 3, 5, 5, 'ADMIN'), -- 새싹 스타트업: 박새싹(경영지원팀/대표, ADMIN)
                                                                                                       (4, 5, NULL, NULL, 'ADMIN'); -- 글로벌 파트너스: 정테스트(미소속, ADMIN)

-- ⭐ 6.5. tbl_email_verification 데이터 (추가)
INSERT INTO `tbl_email_verification` (`verified_email_id`, `user_id`, `token`, `created_at`, `expired_at`, `is_used`) VALUES
                                                                                                                          (1, 3, 'valid_token_for_user3_12345', '2025-07-01 10:00:00.000000', '2025-07-01 11:00:00.000000', FALSE), -- 유효하고 아직 사용되지 않은 토큰
                                                                                                                          (2, 3, 'used_token_for_user3_67890', '2025-06-20 09:00:00.000000', '2025-06-20 10:00:00.000000', TRUE),  -- 이미 사용된 토큰
                                                                                                                          (3, 3, 'expired_token_for_user3_abcde', '2025-05-10 14:00:00.000000', '2025-05-10 15:00:00.000000', FALSE); -- 만료된 토큰

-- ⭐ 6.6. tbl_org_invitations 데이터 (추가)
INSERT INTO `tbl_org_invitations` (`invitation_id`, `org_id`, `inviter_member_id`, `accepted_user_id`, `invited_email`, `invited_code`, `status`, `created_at`, `expires_at`, `accepted_at`) VALUES
                                                                                                                                                                                                 (1, 1, 1, NULL, 'new_member_pending@example.com', 'INVITE_CODE_PENDING_001', 'PENDING', '2025-07-08 10:00:00.000000', '2025-07-15 10:00:00.000000', NULL),  -- 아워아워 -> 새로운 이메일로 초대 (대기 중)
                                                                                                                                                                                                 (2, 2, 2, 2, 'user2@google.com', 'INVITE_CODE_ACCEPTED_002', 'ACCEPTED', '2025-06-01 09:00:00.000000', '2025-06-08 09:00:00.000000', '2025-06-05 14:30:00.000000'), -- 미래솔루션즈 -> user2 (이솔루션) 초대 (수락됨)
                                                                                                                                                                                                 (3, 1, 1, NULL, 'expired_invite@example.com', 'INVITE_CODE_EXPIRED_003', 'EXPIRED', '2025-06-25 11:00:00.000000', '2025-07-02 11:00:00.000000', NULL),  -- 아워아워 -> 만료된 초대
                                                                                                                                                                                                 (4, 3, 3, NULL, 'new_member_pending_2@example.com', 'INVITE_CODE_PENDING_004', 'PENDING', '2025-07-09 16:00:00.000000', '2025-07-16 16:00:00.000000', NULL); -- 새싹 스타트업 -> 새로운 이메일로 초대 (대기 중)
-- 7. tbl_project 데이터
INSERT INTO `tbl_project` (`project_id`, `org_id`, `name`, `description`, `start_at`, `end_at`, `status`) VALUES
                                                                                                              (1, 1, '아워아워 웹 서비스 개발', '메인 웹 서비스 개발 프로젝트', '2024-05-01 09:00:00.000000', '2024-08-31 18:00:00.000000', 'IN_PROGRESS'),
                                                                                                              (2, 1, '모바일 앱 기획', '아워아워 모바일 앱 기획 단계', '2024-06-01 09:00:00.000000', '2024-07-31 18:00:00.000000', 'PLANNING'),
                                                                                                              (3, 2, '영업 자동화 시스템 구축', '미래솔루션즈 영업 프로세스 자동화', '2024-03-01 09:00:00.000000', '2024-05-31 18:00:00.000000', 'DONE'),
                                                                                                              (4, 3, '스타트업 초기 서비스 런칭', '새싹 스타트업의 첫 서비스 런칭', '2024-01-01 09:00:00.000000', '2024-02-28 18:00:00.000000', 'ARCHIVE');


-- 8. tbl_project_participant 데이터
INSERT INTO `tbl_project_participant` (`project_id`, `member_id`) VALUES
                                                                      (1, 1), -- 프로젝트 1: 김아워
                                                                      (1, 2), -- 프로젝트 1: 이솔루션 (다른 조직 멤버지만, 프로젝트 참여 가능하다고 가정)
                                                                      (1, 3), -- 프로젝트 1: 박새싹
                                                                      (2, 1), -- 프로젝트 2: 김아워
                                                                      (3, 2), -- 프로젝트 3: 이솔루션
                                                                      (3, 4); -- 프로젝트 3: 최글로벌


-- 9. tbl_project_participant_department 데이터
INSERT INTO `tbl_project_participant_department` (`project_id`, `dept_id`) VALUES
                                                                               (1, 1), -- 프로젝트 1: 개발팀
                                                                               (1, 2), -- 프로젝트 1: 디자인팀
                                                                               (3, 3); -- 프로젝트 3: 영업팀


-- 10. tbl_milestone 데이터
INSERT INTO `tbl_milestone` (`milestone_id`, `project_id`, `name`, `progress`) VALUES
                                                                                   (1, 1, 'MVP 기능 구현 완료', 70),
                                                                                   (2, 1, '베타 테스트 준비', 20),
                                                                                   (3, 2, '모바일 앱 와이어프레임', 90),
                                                                                   (4, 3, '영업 시스템 최종 검수', 100);


-- 11. tbl_issue_tag 데이터
INSERT INTO `tbl_issue_tag` (`issue_tag_id`, `name`, `color`) VALUES
                                                                  (1, '버그', 'PINK'),
                                                                  (2, '기능 개선', 'BLUE'),
                                                                  (3, '긴급', 'YELLOW'),
                                                                  (4, 'UI/UX', 'GREEN');


-- 12. tbl_issue 데이터
INSERT INTO `tbl_issue` (`issue_id`, `milestone_id`, `issue_tag_id`, `assignee_id`, `name`, `content`, `status`) VALUES
                                                                                                                     (1, 1, 1, 1, '로그인 오류 수정', '로그인 시 간헐적으로 500 에러 발생', 'IN_PROGRESS'),
                                                                                                                     (2, 1, 2, 1, '게시판 검색 기능 개선', '검색 속도 최적화 및 필터 추가', 'BACKLOG'),
                                                                                                                     (3, 2, 4, 2, '모바일 앱 로그인 화면 디자인', '로그인 화면 UI/UX 개선', 'NOT_STARTED'),
                                                                                                                     (4, 3, NULL, 2, '자동화 시스템 최종 보고서 작성', '시스템 구축 결과 보고서', 'COMPLETED'), -- 태그 없음
                                                                                                                     (5, 1, 3, NULL, '결제 모듈 연동 긴급 수정', 'PG사 연동 오류 발생', 'PENDING'); -- 담당자 없음


-- 13. tbl_board 데이터
INSERT INTO `tbl_board` (`board_id`, `org_id`, `name`, `is_fixed`) VALUES
                                                                       (1, 1, '공지사항', TRUE),
                                                                       (2, 1, '자유 게시판', FALSE),
                                                                       (3, 2, '영업 자료실', TRUE),
                                                                       (4, 3, '아이디어 공유', FALSE);


-- 14. tbl_board_fix 데이터
INSERT INTO `tbl_board_fix` (`board_id`, `member_id`) VALUES
                                                          (1, 1), -- 김아워가 공지사항 고정
                                                          (1, 2), -- 이솔루션이 공지사항 고정
                                                          (3, 2); -- 이솔루션이 영업 자료실 고정


-- 15. tbl_post 데이터
INSERT INTO `tbl_post` (`post_id`, `board_id`, `author_id`, `title`, `content`, `created_at`) VALUES
                                                                                                  (1, 1, 1, '주간 업무 보고 안내', '이번 주 업무 보고 양식 및 제출 기한 안내드립니다.', '2024-06-03 10:00:00.000000'),
                                                                                                  (2, 2, 3, '점심 메뉴 추천 받아요!', '오늘 점심 뭐 먹지 고민이네요. 추천 부탁드립니다.', '2024-06-04 12:30:00.000000'),
                                                                                                  (3, 3, 2, '2024년 2분기 영업 실적 보고', '2분기 영업 실적 요약 및 분석 자료입니다.', '2024-06-05 09:00:00.000000'),
                                                                                                  (4, 4, 3, '새로운 서비스 아이디어', '사용자 경험 개선을 위한 새로운 아이디어 제안합니다.', '2024-06-06 15:00:00.000000');


-- 16. tbl_comment 데이터
INSERT INTO `tbl_comment` (`comment_id`, `post_id`, `issue_id`, `author_id`, `parent_comment_id`, `content`, `created_at`) VALUES
                                                                                                                               (1, 2, NULL, 1, NULL, '저도 점심 고민이에요! 근처 파스타집 어떠세요?', '2024-06-04 12:45:00.000000'),
                                                                                                                               (2, 2, NULL, 2, 1, '오, 파스타 좋죠! 상호명 알려주실 수 있나요?', '2024-06-04 13:00:00.000000'), -- 대댓글
                                                                                                                               (3, NULL, 1, 4, NULL, '로그인 오류 재현 확인했습니다. 원인 분석 중입니다.', '2024-06-07 10:00:00.000000'),
                                                                                                                               (4, 4, NULL, 1, NULL, '좋은 아이디어네요! 좀 더 구체적으로 설명해주실 수 있나요?', '2024-06-06 16:00:00.000000');


-- 17. tbl_comment_like 데이터
INSERT INTO `tbl_comment_like` (`comment_id`, `member_id`) VALUES
                                                               (1, 2),
                                                               (1, 4),
                                                               (3, 1);


-- 18. tbl_post_like 데이터
INSERT INTO `tbl_post_like` (`post_id`, `member_id`) VALUES
                                                         (1, 2),
                                                         (1, 3),
                                                         (2, 1),
                                                         (3, 1);


-- 19. tbl_post_bookmark 데이터
INSERT INTO `tbl_post_bookmark` (`post_id`, `member_id`) VALUES
                                                             (1, 1),
                                                             (3, 1),
                                                             (3, 4);


-- 20. tbl_chat_room 데이터
INSERT INTO `tbl_chat_room` (`room_id`, `name`, `color`, `created_at`) VALUES
                                                                           (1, '개발팀 채팅방', 'BLUE', '2024-05-20 10:00:00.000000'),
                                                                           (2, '전체 공지 채팅', 'PINK', '2024-05-25 11:00:00.000000'),
                                                                           (3, '개인 채팅: 김아워-이솔루션', 'GREEN', '2024-06-01 14:00:00.000000');


-- 21. tbl_chat_participant 데이터
INSERT INTO `tbl_chat_participant` (`room_id`, `member_id`) VALUES
                                                                (1, 1), -- 개발팀 채팅방: 김아워
                                                                (1, 3), -- 개발팀 채팅방: 박새싹
                                                                (2, 1), -- 전체 공지: 김아워
                                                                (2, 2), -- 전체 공지: 이솔루션
                                                                (2, 3), -- 전체 공지: 박새싹
                                                                (2, 4), -- 전체 공지: 최글로벌
                                                                (3, 1), -- 개인 채팅: 김아워
                                                                (3, 2); -- 개인 채팅: 이솔루션


-- 22. tbl_chat_message 데이터
INSERT INTO `tbl_chat_message` (`chat_message_id`, `room_id`, `sender_id`, `content`, `sent_at`) VALUES
                                                                                                     (1, 1, 1, '안녕하세요 개발팀 여러분!', '2024-06-01 09:00:00.000000'),
                                                                                                     (2, 1, 3, '네 안녕하세요!', '2024-06-01 09:05:00.000000'),
                                                                                                     (3, 2, 1, '이번주 주간 보고 마감일은 금요일입니다.', '2024-06-03 10:00:00.000000'),
                                                                                                     (4, 3, 1, '이솔루션님, 지난번 프로젝트 건 문의드려요.', '2024-06-02 14:00:00.000000'),
                                                                                                     (5, 3, 2, '네 김아워님, 어떤 내용이신가요?', '2024-06-02 14:05:00.000000');


-- 23. tbl_mail 데이터
INSERT INTO `tbl_mail` (`mail_id`, `member_id`, `title`, `content`, `sent_at`, `received_at`, `email_direction`) VALUES
                                                                                                                     (1, 1, '주간 회의록', '안녕하세요. 주간 회의록입니다.', '2024-06-01 09:00:00.000000', NULL, 'SENT'),
                                                                                                                     (2, 2, '프로젝트 문의', '프로젝트 관련 문의사항입니다.', '2024-06-02 10:00:00.000000', '2024-06-02 10:05:00.000000', 'RECEIVED'),
                                                                                                                     (3, 1, '답변: 프로젝트 문의', '문의하신 프로젝트에 대한 답변입니다.', '2024-06-02 11:00:00.000000', NULL, 'SENT');


-- 24. tbl_email_recipient 데이터
INSERT INTO `tbl_email_recipient` (`mail_id`, `recipient_email`) VALUES
                                                                     (1, 'user2@google.com'),
                                                                     (1, 'user3@github.com'),
                                                                     (2, 'user1@ourhour.dev'),
                                                                     (3, 'user2@google.com');


-- 25. tbl_refresh_token 데이터
INSERT INTO `tbl_refresh_token` (`token_id`, `user_id`, `token`, `created_at`, `expires_at`) VALUES
                                                                                                 (1, 1, 'refresh_token_user1_abcdef123456', '2024-07-01 10:00:00.000000', '2024-07-31 10:00:00.000000'),
                                                                                                 (2, 2, 'refresh_token_user2_ghijkl789012', '2024-07-05 11:00:00.000000', '2024-08-04 11:00:00.000000');