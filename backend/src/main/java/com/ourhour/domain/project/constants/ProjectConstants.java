package com.ourhour.domain.project.constants;

public final class ProjectConstants {

    private ProjectConstants() {
        // 유틸리티 클래스는 인스턴스화 방지
    }

    // 성공 메시지
    public static final String ISSUE_CREATE_SUCCESS = "이슈 등록에 성공했습니다.";
    public static final String ISSUE_UPDATE_SUCCESS = "이슈 수정에 성공했습니다.";
    public static final String ISSUE_DELETE_SUCCESS = "이슈 삭제에 성공했습니다.";
    public static final String ISSUE_STATUS_UPDATE_SUCCESS = "이슈 상태 수정에 성공했습니다.";
    public static final String ISSUE_TAG_CREATE_SUCCESS = "이슈 태그 등록에 성공했습니다.";
    public static final String ISSUE_TAG_UPDATE_SUCCESS = "이슈 태그 수정에 성공했습니다.";
    public static final String ISSUE_TAG_DELETE_SUCCESS = "이슈 태그 삭제에 성공했습니다.";
    public static final String ISSUE_TAG_GET_SUCCESS = "이슈 태그 조회에 성공했습니다.";
    public static final String ISSUE_TAG_NOT_FOUND = "이슈 태그가 존재하지 않습니다.";

    public static final String MILESTONE_ISSUES_MY_SUCCESS = "특정 마일스톤의 내가 할당된 이슈 목록 조회에 성공했습니다.";
    public static final String MILESTONE_ISSUES_UNASSIGNED_MY_SUCCESS = "마일스톤이 할당되지 않은 내가 할당된 이슈 목록 조회에 성공했습니다.";
    public static final String MILESTONE_ISSUES_SUCCESS = "특정 마일스톤의 이슈 목록 조회에 성공했습니다.";
    public static final String MILESTONE_ISSUES_UNASSIGNED_SUCCESS = "마일스톤이 할당되지 않은 이슈 목록 조회에 성공했습니다.";
    public static final String ISSUE_DETAIL_SUCCESS = "이슈 상세 조회에 성공했습니다.";

    public static final String PROJECT_SUMMARY_LIST_SUCCESS = "프로젝트 요약 목록 조회에 성공했습니다.";
    public static final String PROJECT_INFO_SUCCESS = "프로젝트 정보 조회에 성공했습니다.";
    public static final String PROJECT_CREATE_SUCCESS = "프로젝트 등록이 완료되었습니다.";
    public static final String PROJECT_UPDATE_SUCCESS = "프로젝트 수정이 완료되었습니다.";
    public static final String PROJECT_DELETE_SUCCESS = "프로젝트 삭제가 완료되었습니다.";
    public static final String PROJECT_MILESTONE_MY_SUCCESS = "내가 할당된 이슈가 있는 마일스톤 목록 조회에 성공했습니다.";
    public static final String PROJECT_MILESTONE_SUCCESS = "특정 프로젝트의 마일스톤 목록 조회에 성공했습니다.";
}
