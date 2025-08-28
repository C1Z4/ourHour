-- V19: GitHub 사용자명 중복 허용 (동일한 토큰을 여러 사용자가 사용할 수 있도록)

-- GitHub 사용자명의 UNIQUE 제약조건 제거
-- 여러 사용자가 동일한 GitHub 계정의 토큰을 사용할 수 있도록 허용
ALTER TABLE tbl_user_github_token DROP INDEX uk_github_username;