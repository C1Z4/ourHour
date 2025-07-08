DROP TABLE IF EXISTS `tbl_comment_like`;
DROP TABLE IF EXISTS `tbl_post_like`;
DROP TABLE IF EXISTS `tbl_post_bookmark`;
DROP TABLE IF EXISTS `tbl_comment`;
DROP TABLE IF EXISTS `tbl_post`;
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
DROP TABLE IF EXISTS `tbl_member`;
DROP TABLE IF EXISTS `tbl_position`;
DROP TABLE IF EXISTS `tbl_department`;
DROP TABLE IF EXISTS `tbl_org_participant_member`; -- 새로 추가될 테이블의 DROP 문
DROP TABLE IF EXISTS `tbl_org`;
DROP TABLE IF EXISTS `tbl_user`;

CREATE TABLE `tbl_org` (
                           `org_id` BIGINT AUTO_INCREMENT NOT NULL,
                           `name` VARCHAR(100) NOT NULL,
                           `address` VARCHAR(255),
                           `email` VARCHAR(100),
                           `representative_name` VARCHAR(50),
                           `phone` VARCHAR(20),
                           `business_number` VARCHAR(20),
                           `logo_img_url` VARCHAR(512),
                           PRIMARY KEY (`org_id`)
);

CREATE TABLE `tbl_user` (
                            `user_id` BIGINT AUTO_INCREMENT NOT NULL,
                            `email` VARCHAR(100) NOT NULL UNIQUE,
                            `password` VARCHAR(255) NOT NULL,
                            `platform` ENUM('OURHOUR', 'GITHUB', 'GOOGLE', 'KAKAO') NOT NULL,
                            PRIMARY KEY (`user_id`)
);

CREATE TABLE `tbl_department` (
                                  `dept_id` BIGINT AUTO_INCREMENT NOT NULL,
                                  `org_id` BIGINT NOT NULL,
                                  `name` VARCHAR(100),
                                  PRIMARY KEY (`dept_id`),
                                  CONSTRAINT `FK_org_TO_department` FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_position` (
                                `position_id` BIGINT AUTO_INCREMENT NOT NULL,
                                `org_id` BIGINT NOT NULL,
                                `name` VARCHAR(100),
                                PRIMARY KEY (`position_id`),
                                CONSTRAINT `FK_org_TO_position` FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_member` (
                              `member_id` BIGINT AUTO_INCREMENT NOT NULL,
                              `user_id` BIGINT NOT NULL,
                              `name` VARCHAR(50) NOT NULL,
                              `phone` VARCHAR(20),
                              `email` VARCHAR(100) NOT NULL,
                              `role` ENUM('ROOT_ADMIN','ADMIN', 'MEMBER') NOT NULL DEFAULT 'MEMBER',
                              `profile_img_url` VARCHAR(512),
                              PRIMARY KEY (`member_id`),
                              CONSTRAINT `FK_user_TO_member` FOREIGN KEY (`user_id`) REFERENCES `tbl_user`(`user_id`) ON DELETE CASCADE

);

CREATE TABLE `tbl_org_participant_member` (
                                              `org_id` BIGINT NOT NULL,
                                              `member_id` BIGINT NOT NULL,
                                              `dept_id` BIGINT,
                                              `position_id` BIGINT,
                                              `role` ENUM('ROOT_ADMIN','ADMIN', 'MEMBER') NOT NULL DEFAULT 'MEMBER',
                                              PRIMARY KEY (`org_id`, `member_id`),
                                              CONSTRAINT `FK_org_TO_org_participant_member` FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE,
                                              CONSTRAINT `FK_member_TO_org_participant_member` FOREIGN KEY (`member_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE,
                                              CONSTRAINT `FK_department_TO_org_participant_member` FOREIGN KEY (`dept_id`) REFERENCES `tbl_department`(`dept_id`) ON DELETE SET NULL,
                                              CONSTRAINT `FK_position_TO_org_participant_member` FOREIGN KEY (`position_id`) REFERENCES `tbl_position`(`position_id`) ON DELETE SET NULL
);

CREATE TABLE `tbl_refresh_token` (
                                     `token_id` BIGINT AUTO_INCREMENT NOT NULL,
                                     `user_id` BIGINT NOT NULL,
                                     `token` VARCHAR(512) NOT NULL,
                                     `expires_at` DATETIME(6) NOT NULL,
                                     `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                     PRIMARY KEY (`token_id`),
                                     CONSTRAINT `FK_user_TO_refresh_token` FOREIGN KEY (`user_id`) REFERENCES `tbl_user`(`user_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_project` (
                               `project_id` BIGINT AUTO_INCREMENT NOT NULL,
                               `org_id` BIGINT NOT NULL,
                               `name` VARCHAR(255) NOT NULL,
                               `description` TEXT,
                               `start_at` DATETIME(6),
                               `end_at` DATETIME(6),
                               `status` ENUM('NOT_STARTED','PLANNING' ,'IN_PROGRESS', 'DONE', 'ARCHIVE') NOT NULL DEFAULT 'NOT_STARTED',
                               PRIMARY KEY (`project_id`),
                               CONSTRAINT `FK_org_TO_project` FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_project_participant` (
                                           `project_id` BIGINT NOT NULL,
                                           `member_id` BIGINT NOT NULL,
                                           PRIMARY KEY (`project_id`, `member_id`),
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
                                 `progress` TINYINT UNSIGNED NOT NULL DEFAULT 0,
                                 PRIMARY KEY (`milestone_id`),
                                 CONSTRAINT `FK_project_TO_milestone` FOREIGN KEY (`project_id`) REFERENCES `tbl_project`(`project_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_issue_tag` (
                                 `issue_tag_id` BIGINT AUTO_INCREMENT NOT NULL,
                                 `name` VARCHAR(50) NOT NULL,
                                 `color` ENUM('PINK', 'YELLOW', 'GREEN', 'BLUE', 'PURPLE') NOT NULL DEFAULT 'PINK',
                                 PRIMARY KEY (`issue_tag_id`)
);

CREATE TABLE `tbl_issue` (
                             `issue_id` BIGINT AUTO_INCREMENT NOT NULL,
                             `milestone_id` BIGINT NOT NULL,
                             `issue_tag_id` BIGINT,
                             `assignee_id` BIGINT,
                             `name` VARCHAR(255) NOT NULL,
                             `content` TEXT,
                             `status` ENUM('BACKLOG', 'NOT_STARTED', 'PENDING', 'IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'BACKLOG',
                             PRIMARY KEY (`issue_id`),
                             CONSTRAINT `FK_milestone_TO_issue` FOREIGN KEY (`milestone_id`) REFERENCES `tbl_milestone`(`milestone_id`) ON DELETE CASCADE,
                             CONSTRAINT `FK_issue_tag_TO_issue` FOREIGN KEY (`issue_tag_id`) REFERENCES `tbl_issue_tag`(`issue_tag_id`) ON DELETE SET NULL,
                             CONSTRAINT `FK_assignee_TO_issue` FOREIGN KEY (`assignee_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE SET NULL
);

CREATE TABLE `tbl_board` (
                             `board_id` BIGINT AUTO_INCREMENT NOT NULL,
                             `org_id` BIGINT NOT NULL,
                             `name` VARCHAR(100) NOT NULL,
                             `is_fixed` BOOLEAN NOT NULL DEFAULT FALSE,
                             PRIMARY KEY (`board_id`),
                             CONSTRAINT `FK_org_TO_board` FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE
);

CREATE TABLE `tbl_post` (
                            `post_id` BIGINT AUTO_INCREMENT NOT NULL,
                            `board_id` BIGINT,
                            `author_id` BIGINT,
                            `title` VARCHAR(255) NOT NULL,
                            `content` LONGTEXT NOT NULL,
                            `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                            PRIMARY KEY (`post_id`),
                            CONSTRAINT `FK_board_TO_post` FOREIGN KEY (`board_id`) REFERENCES `tbl_board`(`board_id`) ON DELETE CASCADE,
                            CONSTRAINT `FK_member_TO_post` FOREIGN KEY (`author_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE SET NULL
);

CREATE TABLE `tbl_comment` (
                               `comment_id` BIGINT AUTO_INCREMENT NOT NULL,
                               `post_id` BIGINT,
                               `issue_id` BIGINT,
                               `author_id` BIGINT NOT NULL,
                               `parent_comment_id` BIGINT,
                               `content` TEXT NOT NULL,
                               `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                               PRIMARY KEY (`comment_id`),
                               CONSTRAINT `FK_post_TO_comment` FOREIGN KEY (`post_id`) REFERENCES `tbl_post`(`post_id`) ON DELETE CASCADE,
                               CONSTRAINT `FK_issue_TO_comment` FOREIGN KEY (`issue_id`) REFERENCES `tbl_issue`(`issue_id`) ON DELETE CASCADE,
                               CONSTRAINT `FK_member_TO_comment` FOREIGN KEY (`author_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE,
                               CONSTRAINT `FK_parent_comment_TO_comment` FOREIGN KEY (`parent_comment_id`) REFERENCES `tbl_comment`(`comment_id`) ON DELETE CASCADE,
                               CONSTRAINT `check_comment_target` CHECK ((`post_id` IS NOT NULL AND `issue_id` IS NULL) OR (`post_id` IS NULL AND `issue_id` IS NOT NULL))
);

CREATE TABLE tbl_comment_like (
                                  comment_id BIGINT NOT NULL,
                                  author_id BIGINT NOT NULL,
                                  PRIMARY KEY (comment_id, author_id),
                                  FOREIGN KEY (comment_id) REFERENCES tbl_comment(comment_id) ON DELETE CASCADE,
                                  FOREIGN KEY (author_id) REFERENCES tbl_member(member_id) ON DELETE CASCADE
);


CREATE TABLE `tbl_chat_room` (
                                 `room_id` BIGINT AUTO_INCREMENT NOT NULL,
                                 `name` VARCHAR(100),
                                 `color` ENUM('PINK', 'YELLOW', 'GREEN', 'BLUE', 'PURPLE') NOT NULL DEFAULT 'PINK',
                                 `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
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
                                    `room_id` BIGINT NOT NULL,
                                    `sender_id` BIGINT NOT NULL,
                                    `content` TEXT NOT NULL,
                                    `sent_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                    PRIMARY KEY (`chat_message_id`),
                                    CONSTRAINT `FK_chat_room_TO_chat_message` FOREIGN KEY (`room_id`) REFERENCES `tbl_chat_room`(`room_id`) ON DELETE CASCADE,
                                    CONSTRAINT `FK_member_TO_chat_message` FOREIGN KEY (`sender_id`) REFERENCES `tbl_member`(`member_id`) ON DELETE CASCADE
);

CREATE INDEX `idx_post_author` ON `tbl_post`(`author_id`);
CREATE INDEX `idx_issue_assignee` ON `tbl_issue`(`assignee_id`);
CREATE INDEX `idx_chat_message_sent_at` ON `tbl_chat_message`(`sent_at`);


-- =============================================
-- 더미 데이터 생성 스크립트 (다양성 추가)
-- 순서대로 실행해야 외래 키 제약 조건에 위배되지 않습니다.
-- =============================================

-- 1. 조직(회사) 정보 생성 (2개 더 추가)
INSERT INTO `tbl_org` (`name`, `address`, `email`, `representative_name`, `phone`, `business_number`, `logo_img_url`) VALUES
                                                                                                                          ('아워아워', '서울특별시 강남구 테헤란로 123', 'contact@ourhour.dev', '김대표', '02-1234-5678', '123-45-67890', 'https://example.com/logo.png'),
                                                                                                                          ('미래솔루션즈', '서울특별시 서초구 강남대로 500', 'info@futuresolutions.com', '이대표', '02-9876-5432', '987-65-43210', 'https://example.com/logo_future.png'),
                                                                                                                          ('글로벌파트너스', '부산광역시 해운대구 센텀중앙로 100', 'contact@globalpartners.co.kr', '박대표', '051-123-9876', '543-21-09876', 'https://example.com/logo_global.png');

-- 2. 유저 계정 생성 (로그인용) (5개 더 추가)
INSERT INTO `tbl_user` (`email`, `password`, `platform`) VALUES
                                                             ('user1@ourhour.dev', '$2a$10$abcdefghijklmnopqrstuv', 'OURHOUR'),
                                                             ('user2@ourhour.dev', '$2a$10$bcdefghijklmnopqrstuvw', 'OURHOUR'),
                                                             ('user3@ourhour.dev', '$2a$10$cdefghijklmnopqrstuvwx', 'OURHOUR'),
                                                             ('user4@ourhour.dev', '$2a$10$defghijklmnopqrstuvwxy', 'OURHOUR'),
                                                             ('user5@ourhour.dev', '$2a$10$efghijklmnopqrstuvwxyz', 'GOOGLE'),
                                                             ('user6@futuresolutions.com', '$2a$10$fgjklmnopqrstuvwxyzABC', 'OURHOUR'), -- 미래솔루션즈 멤버용
                                                             ('user7@futuresolutions.com', '$2a$10$ghjklmnopqrstuvwxyzBCD', 'OURHOUR'), -- 미래솔루션즈 멤버용
                                                             ('user8@futuresolutions.com', '$2a$10$ijklmnopqrstuvwxyzCDEF', 'OURHOUR'), -- 미래솔루션즈 멤버용
                                                             ('user9@globalpartners.co.kr', '$2a$10$klmnopqrstuvwxyzDEFG', 'OURHOUR'), -- 글로벌파트너스 멤버용
                                                             ('user10@globalpartners.co.kr', '$2a$10$mnopqrstuvwxyzEFGH', 'GOOGLE'); -- 글로벌파트너스 멤버용

-- 3. 부서 정보 생성 (새로운 조직의 부서 추가)
INSERT INTO `tbl_department` (`org_id`, `name`) VALUES
                                                    (1, '개발팀'), (1, '기획팀'), (1, '디자인팀'), -- 아워아워
                                                    (2, '인사팀'), (2, '개발1팀'), (2, '개발2팀'), (2, '사업기획팀'), -- 미래솔루션즈
                                                    (3, '영업1팀'), (3, '영업2팀'), (3, '마케팅팀'), (3, '총무팀'); -- 글로벌파트너스

-- 4. 직급 정보 생성 (새로운 조직의 직급 추가)
INSERT INTO `tbl_position` (`org_id`, `name`) VALUES
                                                  (1, '팀장'), (1, '선임'), (1, '주임'), (1, '사원'), -- 아워아워
                                                  (2, '본부장'), (2, '팀장'), (2, '과장'), (2, '대리'), (2, '사원'), -- 미래솔루션즈 (더 다양하게)
                                                  (3, '이사'), (3, '부장'), (3, '차장'), (3, '과장'), (3, '사원'); -- 글로벌파트너스 (더 다양하게)

-- 5. 멤버(직원) 정보 생성 (새로운 멤버 추가, 역할 관련 컬럼 없음)
INSERT INTO `tbl_member` (`user_id`, `name`, `phone`, `email`, `profile_img_url`) VALUES
                                                                                      (1, '김개발', '010-1111-1111', 'user1@ourhour.dev', 'https://i.pravatar.cc/150?u=user1'),
                                                                                      (2, '이선임', '010-2222-2222', 'user2@ourhour.dev', 'https://i.pravatar.cc/150?u=user2'),
                                                                                      (3, '박기획', '010-3333-3333', 'user3@ourhour.dev', 'https://i.pravatar.cc/150?u=user3'),
                                                                                      (4, '최디자인', '010-4444-4444', 'user4@ourhour.dev', 'https://i.pravatar.cc/150?u=user4'),
                                                                                      (5, '정신입', '010-5555-5555', 'user5@ourhour.dev', 'https://i.pravatar.cc/150?u=user5'),
                                                                                      (6, '김인사', '010-6666-6666', 'user6@futuresolutions.com', 'https://i.pravatar.cc/150?u=user6'), -- 미래솔루션즈 신규 멤버
                                                                                      (7, '이개발', '010-7777-7777', 'user7@futuresolutions.com', 'https://i.pravatar.cc/150?u=user7'), -- 미래솔루션즈 신규 멤버
                                                                                      (8, '박책임', '010-8888-8888', 'user8@futuresolutions.com', 'https://i.pravatar.cc/150?u=user8'), -- 미래솔루션즈 신규 멤버
                                                                                      (9, '최영업', '010-9999-9999', 'user9@globalpartners.co.kr', 'https://i.pravatar.cc/150?u=user9'), -- 글로벌파트너스 신규 멤버
                                                                                      (10, '정마케터', '010-0000-0000', 'user10@globalpartners.co.kr', 'https://i.pravatar.cc/150?u=user10'); -- 글로벌파트너스 신규 멤버

-- 5.5. 조직 참여 멤버 할당 (수정됨: 다양한 조직, 부서, 직급, 역할)
-- Org ID (1: 아워아워, 2: 미래솔루션즈, 3: 글로벌파트너스)
-- Dept ID (아워아워: 1-개발, 2-기획, 3-디자인 / 미래솔루션즈: 4-인사, 5-개발1, 6-개발2, 7-사업기획 / 글로벌파트너스: 8-영업1, 9-영업2, 10-마케팅, 11-총무)
-- Position ID (아워아워: 1-팀장, 2-선임, 3-주임, 4-사원 / 미래솔루션즈: 5-본부장, 6-팀장, 7-과장, 8-대리, 9-사원 / 글로벌파트너스: 10-이사, 11-부장, 12-차장, 13-과장, 14-사원)

INSERT INTO `tbl_org_participant_member` (`org_id`, `member_id`, `dept_id`, `position_id`, `role`) VALUES
-- 아워아워 조직 멤버들
(1, 1, 1, 1, 'ADMIN'),   -- 조직 1 (아워아워)에 멤버 1 (김개발): 개발팀 팀장, 어드민
(1, 2, 1, 2, 'MEMBER'),  -- 조직 1 (아워아워)에 멤버 2 (이선임): 개발팀 선임, 멤버
(1, 3, 2, 3, 'MEMBER'),  -- 조직 1 (아워아워)에 멤버 3 (박기획): 기획팀 주임, 멤버
(1, 4, 3, 4, 'MEMBER'),  -- 조직 1 (아워아워)에 멤버 4 (최디자인): 디자인팀 사원, 멤버
(1, 5, 1, 4, 'MEMBER'),  -- 조직 1 (아워아워)에 멤버 5 (정신입): 개발팀 사원, 멤버

-- 미래솔루션즈 조직 멤버들 (새로운 멤버 포함)
(2, 6, 4, 9, 'MEMBER'),  -- 조직 2 (미래솔루션즈)에 멤버 6 (김인사): 인사팀 사원, 멤버
(2, 7, 5, 7, 'MEMBER'),  -- 조직 2 (미래솔루션즈)에 멤버 7 (이개발): 개발1팀 과장, 멤버
(2, 8, 7, 6, 'ADMIN'),   -- 조직 2 (미래솔루션즈)에 멤버 8 (박책임): 사업기획팀 팀장, 어드민

-- 글로벌파트너스 조직 멤버들 (새로운 멤버 포함)
(3, 9, 8, 12, 'MEMBER'),  -- 조직 3 (글로벌파트너스)에 멤버 9 (최영업): 영업1팀 차장, 멤버
(3, 10, 10, 14, 'MEMBER'),-- 조직 3 (글로벌파트너스)에 멤버 10 (정마케터): 마케팅팀 사원, 멤버

-- 한 멤버가 여러 조직에 속하는 예시
(2, 2, 6, 8, 'MEMBER'),  -- 조직 2 (미래솔루션즈)에 멤버 2 (이선임): 개발2팀 대리 (아워아워와 다른 소속)
(3, 3, 7, 13, 'MEMBER');  -- 조직 3 (글로벌파트너스)에 멤버 3 (박기획): 사업기획팀 과장 (아워아워와 다른 소속)


-- 6. 프로젝트 생성 (변동 없음)
INSERT INTO `tbl_project` (`org_id`, `name`, `description`, `start_at`, `end_at`, `status`) VALUES
                                                                                                (1, '아워아워 그룹웨어 개발', '차세대 그룹웨어 개발 프로젝트', '2025-01-01 09:00:00', '2025-12-31 18:00:00', 'IN_PROGRESS'),
                                                                                                (1, '사내 인트라넷 유지보수', '기존 인트라넷 시스템 유지보수', '2025-06-01 09:00:00', '2026-05-31 18:00:00', 'PLANNING');

-- 7. 프로젝트 참여자 할당 (변동 없음)
INSERT INTO `tbl_project_participant` (`project_id`, `member_id`) VALUES
                                                                      (1, 1), (1, 2), (1, 3), (1, 4), (1, 5);
INSERT INTO `tbl_project_participant` (`project_id`, `member_id`) VALUES
                                                                      (2, 1), (2, 2), (2, 5);

-- 8. 마일스톤 생성 (변동 없음)
INSERT INTO `tbl_milestone` (`project_id`, `name`, `progress`) VALUES
                                                                   (1, '1차 스프린트 (2025-07)', 50),
                                                                   (1, '2차 스프린트 (2025-08)', 0);

-- 9. 이슈 태그 생성 (변동 없음)
INSERT INTO `tbl_issue_tag` (`name`, `color`) VALUES
                                                  ('긴급', 'PINK'),
                                                  ('기능구현', 'BLUE'),
                                                  ('버그', 'YELLOW'),
                                                  ('디자인', 'GREEN');

-- 10. 이슈 생성 (변동 없음)
INSERT INTO `tbl_issue` (`milestone_id`, `issue_tag_id`, `assignee_id`, `name`, `content`, `status`) VALUES
                                                                                                         (1, 2, 2, '채팅 API 설계', 'WebSocket과 STOMP를 이용한 실시간 채팅 기능 설계', 'IN_PROGRESS'),
                                                                                                         (1, 3, 1, '로그인 시 500 에러 발생', '구글 소셜 로그인 시 간헐적으로 500 에러 발생', 'NOT_STARTED'),
                                                                                                         (1, 4, 4, '메인 페이지 UI 시안 작업', '메인 대시보드 UI/UX 디자인', 'COMPLETED');

-- 11. 게시판 생성 (변동 없음)
INSERT INTO `tbl_board` (`org_id`, `name`, `is_fixed`) VALUES
                                                           (1, '공지사항', TRUE),
                                                           (1, '자유게시판', FALSE),
                                                           (1, '개발팀 게시판', FALSE);

-- 12. 게시글 생성 (변동 없음)
INSERT INTO `tbl_post` (`board_id`, `author_id`, `title`, `content`, `created_at`) VALUES
                                                                                       (1, 1, '[필독] 2025년 하계 워크샵 안내', '안녕하세요. 관리자입니다. 8월에 있을 하계 워크샵 관련 공지입니다...', NOW() - INTERVAL 10 DAY),
                                                                                       (2, 3, '점심 메뉴 추천 받습니다!', '오늘 점심 뭐 먹을까요? 강남역 근처 맛집 추천해주세요!', NOW() - INTERVAL 5 DAY),
                                                                                       (3, 2, 'JPA N+1 문제 해결 공유', '최근 프로젝트에서 발생했던 N+1 문제와 해결 과정을 공유합니다.', NOW() - INTERVAL 2 DAY);

-- 13. 댓글 생성 (변동 없음)
INSERT INTO `tbl_comment` (`post_id`, `issue_id`, `author_id`, `parent_comment_id`, `content`, `created_at`) VALUES
                                                                                                                 (1, NULL, 3, NULL, '워크샵 기대되네요!', NOW() - INTERVAL 9 DAY),
                                                                                                                 (2, NULL, 1, NULL, '저는 부대찌개 한 표요!', NOW() - INTERVAL 5 DAY),
                                                                                                                 (2, NULL, 2, 2, '오 부대찌개 좋죠', NOW() - INTERVAL 4 DAY),
                                                                                                                 (NULL, 1, 1, NULL, '이선임님, 해당 이슈 확인 부탁드립니다.', NOW() - INTERVAL 1 DAY);

INSERT INTO tbl_comment_like (comment_id, author_id) VALUES
                                                         (1, 1),
                                                         (1, 2),
                                                         (2, 1),
                                                         (3, 3);

-- 14. 채팅방 생성 (변동 없음)
INSERT INTO `tbl_chat_room` (`name`, `color`) VALUES
                                                  ('그룹웨어 개발팀 단톡방', 'PINK'),
                                                  ('점심팟', 'YELLOW'),
                                                  ('김개발-박기획 1:1 대화방', 'PURPLE');

-- 15. 채팅방 참여자 할당 (변동 없음)
INSERT INTO `tbl_chat_participant` (`room_id`, `member_id`) VALUES
                                                                (1, 1), (1, 2), (1, 5);
INSERT INTO `tbl_chat_participant` (`room_id`, `member_id`) VALUES
                                                                (2, 1), (2, 2), (2, 3), (2, 4), (2, 5);
INSERT INTO `tbl_chat_participant` (`room_id`, `member_id`) VALUES
                                                                (3, 1), (3, 3);

-- 16. 채팅 메시지 생성 (대화 시나리오) (변동 없음)
INSERT INTO `tbl_chat_message` (`room_id`, `sender_id`, `content`, `sent_at`) VALUES
                                                                                  (1, 1, '이선임님, 어제 공유드린 API 명세 초안 확인하셨을까요?', NOW() - INTERVAL 1 HOUR),
                                                                                  (1, 2, '네 팀장님. 확인했고, 메시지 DTO에 타임스탬프 필드 추가하면 좋을 것 같습니다.', NOW() - INTERVAL 59 MINUTE),
                                                                                  (1, 1, '좋은 의견이네요. 바로 반영하겠습니다.', NOW() - INTERVAL 58 MINUTE),
                                                                                  (1, 5, '선배님들 저도 뭐 도울 거 없을까요? 🔥', NOW() - INTERVAL 50 MINUTE);
INSERT INTO `tbl_chat_message` (`room_id`, `sender_id`, `content`, `sent_at`) VALUES
                                                                                  (2, 3, '오늘 점심 뭐먹지..', NOW() - INTERVAL 2 HOUR),
                                                                                  (2, 4, '디자인팀은 돈까스 먹으러 갈까 하는데 같이 가실 분?', NOW() - INTERVAL 50 MINUTE),
                                                                                  (2, 1, '오 좋아요! 저희도 같이 가겠습니다.', NOW() - INTERVAL 45 MINUTE);