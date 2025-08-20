package com.ourhour.domain.project.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class GithubException extends BusinessException {

    public GithubException(ErrorCode errorCode) {
        super(errorCode);
    }

    public GithubException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static GithubException githubTokenNotFoundException() {
        return new GithubException(ErrorCode.GITHUB_TOKEN_NOT_FOUND);
    }

    public static GithubException githubTokenSaveFailedException() {
        return new GithubException(ErrorCode.GITHUB_TOKEN_SAVE_FAILED);
    }

    public static GithubException githubTokenUpdateFailedException() {
        return new GithubException(ErrorCode.GITHUB_TOKEN_UPDATE_FAILED);
    }

    public static GithubException githubTokenDeleteFailedException() {
        return new GithubException(ErrorCode.GITHUB_TOKEN_DELETE_FAILED);
    }

    public static GithubException githubTokenNotMatchException() {
        return new GithubException(ErrorCode.GITHUB_TOKEN_NOT_MATCH);
    }

    public static GithubException githubTokenNotAuthorizedException() {
        return new GithubException(ErrorCode.GITHUB_TOKEN_NOT_AUTHORIZED);
    }

    public static GithubException githubRepositoryNotFoundException() {
        return new GithubException(ErrorCode.GITHUB_REPOSITORY_NOT_FOUND);
    }

    public static GithubException githubRepositoryAlreadyConnectedException() {
        return new GithubException(ErrorCode.GITHUB_REPOSITORY_ALREADY_CONNECTED);
    }

    public static GithubException githubMilestoneListNotFoundException() {
        return new GithubException(ErrorCode.GITHUB_MILESTONE_LIST_NOT_FOUND);
    }

    public static GithubException githubRepositoryAccessDeniedException() {
        return new GithubException(ErrorCode.GITHUB_REPOSITORY_ACCESS_DENIED);
    }

    public static GithubException invalidRepositoryFormatException() {
        return new GithubException(ErrorCode.INVALID_REPOSITORY_FORMAT);
    }

    public static GithubException githubSyncFailedException() {
        return new GithubException(ErrorCode.GITHUB_SYNC_FAILED);
    }

    public static GithubException githubIntegrationNotFoundException() {
        return new GithubException(ErrorCode.GITHUB_INTEGRATION_NOT_FOUND);
    }

}
