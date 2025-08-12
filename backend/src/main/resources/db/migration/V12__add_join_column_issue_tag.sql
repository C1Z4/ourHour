-- 기존 이슈 태그 데이터 삭제
DELETE FROM tbl_issue_tag;

-- 이슈 태그 테이블에 프로젝트 조인 컬럼 추가
ALTER TABLE tbl_issue_tag
ADD COLUMN project_id BIGINT NOT NULL,
ADD CONSTRAINT fk_issue_tag_project_id FOREIGN KEY (project_id) REFERENCES tbl_project(project_id) ON DELETE CASCADE;


