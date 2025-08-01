package com.ourhour.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // ========== HTTP 표준 상태 코드 ==========
    INVALID_REQUEST("유효하지 않은 요청입니다", 400, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("인증 정보가 없습니다", 401, HttpStatus.UNAUTHORIZED),
    FORBIDDEN("접근 권한이 없습니다", 403, HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다", 500, HttpStatus.INTERNAL_SERVER_ERROR),

    // ========== 사용자 관련 ==========
    USER_NOT_FOUND("사용자를 찾을 수 없습니다", 1000, HttpStatus.NOT_FOUND),
    USER_NOT_AUTHORIZED("사용자 인증에 실패했습니다", 1001, HttpStatus.UNAUTHORIZED),

    // ========== 이메일 관련 ==========
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다", 1002, HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND("이메일이 존재하지 않습니다", 1003, HttpStatus.NOT_FOUND),
    EMAIL_SEND_FAILED("이메일 발송에 실패했습니다", 1007, HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_VERIFICATION_EXPIRED("이메일 인증 링크가 만료되었습니다", 1008, HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_VERIFIED("이미 인증된 이메일 인증 링크 입니다", 1009, HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_VERIFICATION_TOKEN("유효하지 않은 이메일 인증 링크입니다", 1010, HttpStatus.BAD_REQUEST),
    EMAIL_VERIFICATION_REQUIRED("이메일 인증 먼저 해주세요", 1011, HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_ACCEPTED("이미 참여가 완료된 이메일 링크입니다", 1012, HttpStatus.BAD_REQUEST),
    EMAIL_NOT_MATCH("로그인된 계정의 이메일과 초대받은 이메일이 일치하지 않습니다", 1013, HttpStatus.BAD_REQUEST),

    // ========== 비밀번호 관련 ==========
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다", 1004, HttpStatus.BAD_REQUEST),
    SAME_AS_PREVIOUS_PASSWORD("이전 비밀번호와 동일합니다", 400, HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_NOT_MATCH("새 비밀번호가 일치하지 않습니다", 400, HttpStatus.BAD_REQUEST),

    // ========== 토큰 관련 ==========
    INVALID_TOKEN("토큰이 유효하지 않습니다", 401, HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND("토큰이 존재하지 않습니다", 1005, HttpStatus.NOT_FOUND),

    // ========== 계정 상태 관련 ==========
    DEACTIVATED_ACCOUNT("탈퇴처리된 계정입니다", 1006, HttpStatus.BAD_REQUEST),
    ROOT_ADMIN_ROLE_CONFLICT("탈퇴하려면 다른 루트 관리자를 지정하거나 권한을 변경해야 합니다", 400, HttpStatus.BAD_REQUEST),

    // ========== 구성원 관련 ==========
    MEMBER_NOT_FOUND("구성원을 찾을 수 없습니다", 3000, HttpStatus.NOT_FOUND),
    MEMBER_ACCESS_DENIED("구성원 접근 권한이 없습니다", 3001, HttpStatus.FORBIDDEN),
    MEMBER_ALREADY_EXISTS("이미 존재하는 구성원입니다", 3002, HttpStatus.BAD_REQUEST),

    // ========== 조직 관련 ==========
    ORG_NOT_FOUND("회사를 찾을 수 없습니다", 4000, HttpStatus.NOT_FOUND),
    ORG_ACCESS_DENIED("회사 접근 권한이 없습니다", 4001, HttpStatus.FORBIDDEN),
    ORG_ALREADY_EXISTS("이미 존재하는 회사입니다", 4002, HttpStatus.BAD_REQUEST),
    ORG_MEMBER_NOT_FOUND("회사 구성원을 찾을 수 없습니다", 4003, HttpStatus.NOT_FOUND),
    ORG_ID_NOT_MATCH("해당 회사의 소속이 아니거나 회사 아이디의 값이 잘못되었습니다", 4004, HttpStatus.BAD_REQUEST),

    // ========== 루트 관리자 관련 ==========
    ROOT_ADMIN_MINIMUM_REQUIRED("루트 관리자는 최소 한명 이상이어야 합니다", 10000, HttpStatus.BAD_REQUEST),
    ROOT_ADMIN_MAXIMUM_EXCEEDED("루트 관리자는 최대 2명이어야 합니다", 10001, HttpStatus.BAD_REQUEST),
    LAST_ROOT_ADMIN_CANNOT_LEAVE("마지막 루트 관리자입니다. 위임 후 계정 탈퇴 가능합니다", 10002, HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_SELF("자기 자신은 삭제할 수 없습니다. 다른 루트 관리자에게 삭제를 요청하세요", 10003, HttpStatus.BAD_REQUEST),

    // ========== 프로젝트 관련 ==========
    PROJECT_NOT_FOUND("프로젝트를 찾을 수 없습니다", 5000, HttpStatus.NOT_FOUND),
    PROJECT_ACCESS_DENIED("프로젝트 접근 권한이 없습니다", 5001, HttpStatus.FORBIDDEN),
    PROJECT_PARTICIPANT_REQUIRED("프로젝트 참여자만 접근할 수 있습니다", 5002, HttpStatus.BAD_REQUEST),
    PROJECT_PARTICIPANT_LIMIT_INVALID("참여자 제한 수는 1 이상이어야 합니다", 5003, HttpStatus.BAD_REQUEST),
    PROJECT_PARTICIPANT_OR_ADMIN_OR_ROOT_ADMIN("프로젝트 참여자이거나 ADMIN 이상 권한이 있어야 합니다", 5004, HttpStatus.BAD_REQUEST),
    PROJECT_ID_REQUIRED("프로젝트 ID는 필수입니다", 5005, HttpStatus.BAD_REQUEST),
    // ========== 마일스톤 관련 ==========
    MILESTONE_NOT_FOUND("마일스톤을 찾을 수 없습니다", 5100, HttpStatus.NOT_FOUND),
    MILESTONE_NAME_DUPLICATE("이미 존재하는 마일스톤 이름입니다", 5101, HttpStatus.BAD_REQUEST),

    // ========== 이슈 관련 ==========
    ISSUE_NOT_FOUND("이슈를 찾을 수 없습니다", 5200, HttpStatus.NOT_FOUND),

    // ========== 게시판 관련 ==========
    BOARD_NOT_FOUND("게시판을 찾을 수 없습니다", 6000, HttpStatus.NOT_FOUND),
    BOARD_AUTHOR_NOT_FOUND("해당 작성자를 찾을 수 없습니다", 6001, HttpStatus.NOT_FOUND),
    POST_NOT_FOUND("게시글을 찾을 수 없습니다", 6002, HttpStatus.NOT_FOUND),
    POST_ACCESS_DENIED("해당 게시글을 조회할 권한이 없습니다", 6003, HttpStatus.FORBIDDEN),
    POST_AUTHOR_NOT_FOUND("해당 작성자를 찾을 수 없습니다", 6004, HttpStatus.NOT_FOUND),
    POST_AUTHOR_ACCESS_DENIED("해당 게시글을 수정할 권한이 없습니다", 6005, HttpStatus.FORBIDDEN),
    POST_AUTHOR_DELETE_ACCESS_DENIED("해당 게시글을 삭제할 권한이 없습니다", 6006, HttpStatus.FORBIDDEN),
    POST_UPDATE_ACCESS_DENIED("해당 게시글을 수정할 권한이 없습니다", 6007, HttpStatus.FORBIDDEN),

    // ========== 댓글 관련 ==========
    COMMENT_NOT_FOUND("존재하지 않는 댓글입니다", 7000, HttpStatus.NOT_FOUND),
    COMMENT_CONTENT_REQUIRED("댓글 내용은 필수입니다", 7001, HttpStatus.BAD_REQUEST),
    COMMENT_CONTENT_TOO_LONG("댓글 내용은 1000자를 초과할 수 없습니다", 7002, HttpStatus.BAD_REQUEST),
    COMMENT_AUTHOR_REQUIRED("작성자 ID는 필수입니다", 7003, HttpStatus.BAD_REQUEST),
    COMMENT_TARGET_REQUIRED("postId 또는 issueId 중 하나는 필수입니다", 7004, HttpStatus.BAD_REQUEST),
    COMMENT_TARGET_CONFLICT("postId 또는 issueId 중 하나만 입력해주세요", 7005, HttpStatus.BAD_REQUEST),

    // ========== 채팅 관련 ==========
    CHAT_ROOM_NOT_FOUND("존재하지 않는 채팅방입니다", 8000, HttpStatus.NOT_FOUND),
    CHAT_NOT_PARTICIPANT("해당 채팅방의 참여자가 아닙니다", 8001, HttpStatus.FORBIDDEN),
    CHAT_ALREADY_PARTICIPANT("이미 참여하고 있는 채팅방입니다", 8002, HttpStatus.BAD_REQUEST),

    // ========== 파일 관련 ==========
    INVALID_FILE_FORMAT("잘못된 파일 형식입니다", 9000, HttpStatus.BAD_REQUEST),
    FILE_SAVE_ERROR("파일 저장 중 오류가 발생했습니다", 9001, HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_BASE64_FORMAT("잘못된 Base64 형식입니다", 9002, HttpStatus.BAD_REQUEST);

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
}
