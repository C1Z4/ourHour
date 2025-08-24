package com.ourhour.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // ========== HTTP 표준 상태 코드 ==========
    INVALID_REQUEST("유효하지 않은 요청입니다", 400, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("인증 정보가 없습니다", 401, HttpStatus.UNAUTHORIZED),
    FORBIDDEN("접근 권한이 없습니다", 403, HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다", 500, HttpStatus.INTERNAL_SERVER_ERROR),

    // ========== 사용자 관련 (1000~1999) ==========
    USER_NOT_FOUND("사용자를 찾을 수 없습니다", 1000, HttpStatus.NOT_FOUND),
    USER_NOT_AUTHORIZED("사용자 인증에 실패했습니다", 1001, HttpStatus.UNAUTHORIZED),

    // 이메일 관련
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다", 1002, HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND("이메일이 존재하지 않습니다", 1003, HttpStatus.NOT_FOUND),
    EMAIL_SEND_FAILED("이메일 발송에 실패했습니다", 1004, HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_VERIFICATION_EXPIRED("이메일 인증 링크가 만료되었습니다", 1005, HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_VERIFIED("이미 인증된 이메일 인증 링크 입니다", 1006, HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_VERIFICATION_TOKEN("유효하지 않은 이메일 인증 링크입니다", 1007, HttpStatus.BAD_REQUEST),
    EMAIL_VERIFICATION_REQUIRED("이메일 인증 먼저 해주세요", 1008, HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_ACCEPTED("이미 참여가 완료된 이메일 링크입니다", 1009, HttpStatus.BAD_REQUEST),
    EMAIL_NOT_MATCH("로그인된 계정의 이메일과 초대받은 이메일이 일치하지 않습니다", 1010, HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("올바르지 않은 이메일 형식입니다.", 1011, HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED_FOR_GITHUB("깃허브 이메일이 비공개입니다. 이메일을 필수로 입력해주세요.", 1012, HttpStatus.BAD_REQUEST),

    // 비밀번호 관련
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다", 1020, HttpStatus.BAD_REQUEST),
    SAME_AS_PREVIOUS_PASSWORD("이전 비밀번호와 동일합니다", 1021, HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_NOT_MATCH("새 비밀번호가 일치하지 않습니다", 1022, HttpStatus.BAD_REQUEST),
    PWD_REQUIRED_FOR_GITHUB("소셜 로그인을 위한 회사 관리 비밀번호는 필수입니다.", 1023, HttpStatus.BAD_REQUEST),

    // 토큰 관련
    INVALID_TOKEN("토큰이 유효하지 않습니다", 1030, HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND("토큰이 존재하지 않습니다", 1031, HttpStatus.NOT_FOUND),

    // 계정 상태 관련
    DEACTIVATED_ACCOUNT("탈퇴처리된 계정입니다", 1040, HttpStatus.BAD_REQUEST),
    ROOT_ADMIN_ROLE_CONFLICT("탈퇴하려면 다른 루트 관리자를 지정하거나 권한을 변경해야 합니다", 1041, HttpStatus.BAD_REQUEST),

    // ========== 구성원 관련 (2000~2999) ==========
    MEMBER_NOT_FOUND("구성원을 찾을 수 없습니다", 2000, HttpStatus.NOT_FOUND),
    MEMBER_ACCESS_DENIED("구성원 접근 권한이 없습니다", 2001, HttpStatus.FORBIDDEN),
    MEMBER_ALREADY_EXISTS("이미 존재하는 구성원입니다", 2002, HttpStatus.BAD_REQUEST),

    // 루트 관리자 관련
    ROOT_ADMIN_MINIMUM_REQUIRED("루트 관리자는 최소 한명 이상이어야 합니다", 2003, HttpStatus.BAD_REQUEST),
    ROOT_ADMIN_MAXIMUM_EXCEEDED("루트 관리자는 최대 2명이어야 합니다", 2004, HttpStatus.BAD_REQUEST),
    LAST_ROOT_ADMIN_CANNOT_LEAVE("마지막 루트 관리자입니다. 위임 후 계정 탈퇴 가능합니다", 2005, HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_SELF("자기 자신은 삭제할 수 없습니다. 다른 루트 관리자에게 삭제를 요청하세요", 2006, HttpStatus.BAD_REQUEST),

    // 구성원 초대 관련
    CANNOT_INVITE_SELF("자기 자신은 초대할 수 없습니다.", 2007, HttpStatus.BAD_REQUEST),

    // ========== 회사 관련 (3000~3999) ==========
    ORG_NOT_FOUND("회사를 찾을 수 없습니다", 3000, HttpStatus.NOT_FOUND),
    ORG_ACCESS_DENIED("회사 접근 권한이 없습니다", 3001, HttpStatus.FORBIDDEN),
    ORG_ALREADY_EXISTS("이미 존재하는 회사입니다", 3002, HttpStatus.BAD_REQUEST),
    ORG_MEMBER_NOT_FOUND("회사 구성원을 찾을 수 없습니다", 3003, HttpStatus.NOT_FOUND),
    ORG_ID_NOT_MATCH("해당 회사의 소속이 아니거나 회사 아이디의 값이 잘못되었습니다", 3004, HttpStatus.BAD_REQUEST),

    // 부서 관련
    DEPARTMENT_NOT_FOUND("부서를 찾을 수 없습니다", 3010, HttpStatus.NOT_FOUND),
    DEPARTMENT_NAME_DUPLICATE("이미 존재하는 부서명입니다", 3011, HttpStatus.BAD_REQUEST),
    DEPARTMENT_HAS_MEMBERS("해당 부서에 소속된 구성원이 있어 삭제할 수 없습니다", 3012, HttpStatus.BAD_REQUEST),

    // 직책 관련
    POSITION_NOT_FOUND("직책을 찾을 수 없습니다", 3020, HttpStatus.NOT_FOUND),
    POSITION_NAME_DUPLICATE("이미 존재하는 직책명입니다", 3021, HttpStatus.BAD_REQUEST),
    POSITION_HAS_MEMBERS("해당 직책에 소속된 구성원이 있어 삭제할 수 없습니다", 3022, HttpStatus.BAD_REQUEST),

    // ========== 프로젝트 관련 (4000~4999) ==========
    PROJECT_NOT_FOUND("프로젝트를 찾을 수 없습니다", 4000, HttpStatus.NOT_FOUND),
    PROJECT_ACCESS_DENIED("프로젝트 접근 권한이 없습니다", 4001, HttpStatus.FORBIDDEN),
    PROJECT_PARTICIPANT_REQUIRED("프로젝트 참여자만 접근할 수 있습니다", 4002, HttpStatus.BAD_REQUEST),
    PROJECT_PARTICIPANT_LIMIT_INVALID("참여자 제한 수는 1 이상이어야 합니다", 4003, HttpStatus.BAD_REQUEST),
    PROJECT_PARTICIPANT_OR_ADMIN_OR_ROOT_ADMIN("프로젝트 참여자이거나 ADMIN 이상 권한이 있어야 합니다", 4004, HttpStatus.BAD_REQUEST),
    PROJECT_ID_REQUIRED("프로젝트 ID는 필수입니다", 4005, HttpStatus.BAD_REQUEST),

    // 마일스톤 관련
    MILESTONE_NOT_FOUND("마일스톤을 찾을 수 없습니다", 4006, HttpStatus.NOT_FOUND),
    MILESTONE_NAME_DUPLICATE("이미 존재하는 마일스톤 이름입니다", 4007, HttpStatus.BAD_REQUEST),

    // 이슈 관련
    ISSUE_NOT_FOUND("이슈를 찾을 수 없습니다", 4008, HttpStatus.NOT_FOUND),
    ISSUE_TAG_NOT_FOUND("이슈 태그를 찾을 수 없습니다", 4009, HttpStatus.NOT_FOUND),
    // ========== 게시판 관련 (5000~5999) ==========
    BOARD_NOT_FOUND("게시판을 찾을 수 없습니다", 5000, HttpStatus.NOT_FOUND),
    BOARD_AUTHOR_NOT_FOUND("해당 작성자를 찾을 수 없습니다", 5001, HttpStatus.NOT_FOUND),
    POST_NOT_FOUND("게시글을 찾을 수 없습니다", 5002, HttpStatus.NOT_FOUND),
    POST_ACCESS_DENIED("해당 게시글을 조회할 권한이 없습니다", 5003, HttpStatus.FORBIDDEN),
    POST_AUTHOR_NOT_FOUND("해당 작성자를 찾을 수 없습니다", 5004, HttpStatus.NOT_FOUND),
    POST_AUTHOR_ACCESS_DENIED("해당 게시글을 수정할 권한이 없습니다", 5005, HttpStatus.FORBIDDEN),
    POST_AUTHOR_DELETE_ACCESS_DENIED("해당 게시글을 삭제할 권한이 없습니다", 5006, HttpStatus.FORBIDDEN),
    POST_UPDATE_ACCESS_DENIED("해당 게시글을 수정할 권한이 없습니다", 5007, HttpStatus.FORBIDDEN),

    // ========== 댓글 관련 (6000~6999) ==========
    COMMENT_NOT_FOUND("존재하지 않는 댓글입니다", 6000, HttpStatus.NOT_FOUND),
    COMMENT_CONTENT_REQUIRED("댓글 내용은 필수입니다", 6001, HttpStatus.BAD_REQUEST),
    COMMENT_CONTENT_TOO_LONG("댓글 내용은 1000자를 초과할 수 없습니다", 6002, HttpStatus.BAD_REQUEST),
    COMMENT_AUTHOR_REQUIRED("작성자 ID는 필수입니다", 6003, HttpStatus.BAD_REQUEST),
    COMMENT_TARGET_REQUIRED("postId 또는 issueId 중 하나는 필수입니다", 6004, HttpStatus.BAD_REQUEST),
    COMMENT_TARGET_CONFLICT("postId 또는 issueId 중 하나만 입력해주세요", 6005, HttpStatus.BAD_REQUEST),
    COMMENT_ALREADY_LIKED("이미 좋아요를 누른 댓글입니다", 6006, HttpStatus.BAD_REQUEST),
    COMMENT_LIKE_NOT_FOUND("좋아요를 누르지 않은 댓글입니다", 6007, HttpStatus.NOT_FOUND),

    // ========== 채팅 관련 (7000~7999) ==========
    CHAT_ROOM_NOT_FOUND("존재하지 않는 채팅방입니다", 7000, HttpStatus.NOT_FOUND),
    CHAT_NOT_PARTICIPANT("해당 채팅방의 참여자가 아닙니다", 7001, HttpStatus.FORBIDDEN),
    CHAT_ALREADY_PARTICIPANT("이미 참여하고 있는 채팅방입니다", 7002, HttpStatus.BAD_REQUEST),

    // ========== 파일 관련 (8000~8999) ==========
    INVALID_FILE_FORMAT("잘못된 파일 형식입니다", 8000, HttpStatus.BAD_REQUEST),
    FILE_SAVE_ERROR("파일 저장 중 오류가 발생했습니다", 8001, HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_BASE64_FORMAT("잘못된 Base64 형식입니다", 8002, HttpStatus.BAD_REQUEST),

    // ========== 깃허브 관련 (9000~9999) ==========
    GITHUB_TOKEN_NOT_FOUND("깃허브 토큰을 찾을 수 없습니다", 9000, HttpStatus.NOT_FOUND),
    GITHUB_TOKEN_SAVE_FAILED("깃허브 토큰 저장에 실패했습니다", 9001, HttpStatus.INTERNAL_SERVER_ERROR),
    GITHUB_TOKEN_UPDATE_FAILED("깃허브 토큰 업데이트에 실패했습니다", 9002, HttpStatus.INTERNAL_SERVER_ERROR),
    GITHUB_TOKEN_DELETE_FAILED("깃허브 토큰 삭제에 실패했습니다", 9003, HttpStatus.INTERNAL_SERVER_ERROR),
    GITHUB_TOKEN_NOT_MATCH("깃허브 토큰이 일치하지 않습니다", 9004, HttpStatus.BAD_REQUEST),
    GITHUB_TOKEN_NOT_AUTHORIZED("깃허브 토큰이 인증되지 않았습니다", 9005, HttpStatus.UNAUTHORIZED),
    GITHUB_REPOSITORY_NOT_FOUND("깃허브 레포지토리를 찾을 수 없습니다", 9006, HttpStatus.NOT_FOUND),
    GITHUB_REPOSITORY_ALREADY_CONNECTED("이미 깃허브 레포지토리가 연동되어 있습니다", 9007, HttpStatus.BAD_REQUEST),
    GITHUB_MILESTONE_LIST_NOT_FOUND("깃허브 마일스톤 목록을 찾을 수 없습니다", 9008, HttpStatus.NOT_FOUND),
    GITHUB_REPOSITORY_ACCESS_DENIED("깃허브 레포지토리에 접근할 수 없습니다", 9009, HttpStatus.FORBIDDEN),
    INVALID_REPOSITORY_FORMAT("깃허브 레포지토리 형식이 올바르지 않습니다", 9010, HttpStatus.BAD_REQUEST),
    GITHUB_SYNC_FAILED("깃허브 동기화에 실패했습니다", 9011, HttpStatus.INTERNAL_SERVER_ERROR),
    GITHUB_INTEGRATION_NOT_FOUND("깃허브 연동 정보를 찾을 수 없습니다", 9012, HttpStatus.NOT_FOUND),

    // ========== 알림 관련 (10000~10999) ==========
    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다", 10000, HttpStatus.NOT_FOUND),
    NOTIFICATION_ACCESS_DENIED("알림에 접근할 권한이 없습니다", 10001, HttpStatus.FORBIDDEN),
    SSE_CONNECTION_ERROR("실시간 알림 연결에 실패했습니다", 10002, HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final int statusCode;
    private final HttpStatus status;

    ErrorCode(String message, int statusCode, HttpStatus status) {
        this.message = message;
        this.statusCode = statusCode;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
