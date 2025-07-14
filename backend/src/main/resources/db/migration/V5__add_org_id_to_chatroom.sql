ALTER TABLE `tbl_chat_room`
ADD COLUMN `org_id` BIGINT NULL;

UPDATE `tbl_chat_room` cr
SET cr.org_id = (
    SELECT opm.org_id
    FROM `tbl_chat_participant` cp
             JOIN `tbl_org_participant_member` opm ON cp.member_id = opm.member_id
    WHERE cp.room_id = cr.room_id
    LIMIT 1
)
WHERE cr.org_id IS NULL;

ALTER TABLE `tbl_chat_room`
MODIFY COLUMN org_id BIGINT NOT NULL;

ALTER TABLE `tbl_chat_room`
    ADD CONSTRAINT FK_org_TO_chat_room
        FOREIGN KEY (`org_id`) REFERENCES `tbl_org`(`org_id`) ON DELETE CASCADE;

INSERT INTO `tbl_chat_room` (`room_id`, `org_id`, `name`, `color`, `created_at`) VALUES
                                                                                     (4, 1, '나 자신과의 대화 📝', 'PURPLE', '2024-06-10 10:00:00.000000'),
                                                                                     (5, 1, '디자인팀 전용', 'YELLOW', '2024-06-11 11:00:00.000000');


INSERT INTO `tbl_chat_participant` (`room_id`, `member_id`) VALUES
                                                                (4, 5), -- 나 자신과의 대화방 참여자
                                                                (5, 1),
                                                                (5, 3);



INSERT INTO `tbl_chat_message` (`room_id`, `sender_id`, `content`, `sent_at`) VALUES
-- 기존 "개발팀 채팅방"에 특수문자 메시지 추가
(1, 1, 'DB 마이그레이션 스크립트 특수문자 테스트! `!@#$%^&*()_+-=`', '2024-06-12 15:00:00.000000'),

-- "나 자신과의 대화"방
(4, 5, '오늘 할 일 정리하기! 잊지 말자! 🚀', '2024-06-10 10:01:00.000000'),
(4, 5, 'DB 설계 수정... API 명세서도 바꿔야 하고... 할 게 많네... 🤯', '2024-06-10 10:02:00.000000'),

(5, 1, '새로운 로고 시안 공유드립니다. 피드백 부탁드려요~', '2024-06-11 11:05:00.000000'),
(5, 3, '우와, 너무 멋진데요? 👍 피드백 정리해서 전달드릴게요!', '2024-06-11 11:10:00.000000');

