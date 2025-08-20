export const PROJECT_QUERY_KEYS = {
  SUMMARY_LIST: 'projectSummaryList',
  MILESTONE_LIST: 'projectMilestoneList',
  ISSUE_LIST: 'projectIssueList',
  ISSUE_DETAIL: 'projectIssueDetail',
  ISSUE_TAG_LIST: 'projectIssueTagList',
  PROJECT_INFO: 'projectInfo',
  PARTICIPANT_LIST: 'projectParticipantList',
  GITHUB_SYNC_STATUS: 'githubSyncStatus',
} as const;

export const ORG_QUERY_KEYS = {
  MEMBER_LIST: 'orgMemberList',
  MEMBER_LIST_ALL: 'orgMemberListAll',
  MY_ORG_LIST: 'myOrgList',
  MY_PROJECT_LIST: 'myProjectList',
  ORG_INFO: 'orgInfo',
  DEPARTMENT_LIST: 'departmentList',
  POSITION_LIST: 'positionList',
  DEPARTMENT_MEMBERS: 'departmentMembers',
  POSITION_MEMBERS: 'positionMembers',
} as const;

export const MEMBER_QUERY_KEYS = {
  MY_MEMBER_INFO: 'myMemberInfo',
} as const;

export const COMMENT_QUERY_KEYS = {
  COMMENT_LIST: 'commentList',
} as const;

export const BOARD_QUERY_KEYS = {
  BOARD_LIST: 'boardList',
  ALL_POST_LIST: 'allPostList',
  POST_LIST: 'postList',
  POST_DETAIL: 'postDetail',
} as const;

export const CHAT_QUERY_KEYS = {
  CHAT_LIST: 'chatList',
  CHAT_DETAIL: 'chatDetail',
} as const;
