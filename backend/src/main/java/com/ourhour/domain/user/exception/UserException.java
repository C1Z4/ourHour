package com.ourhour.domain.user.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class UserException extends BusinessException {

  public UserException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public static UserException passwordNotMatch() {
    return new UserException(ErrorCode.PASSWORD_NOT_MATCH);
  }

  public static UserException notMatchPwd() {
    return new UserException(ErrorCode.NEW_PASSWORD_NOT_MATCH);
  }

  public static UserException samePwd() {
    return new UserException(ErrorCode.SAME_AS_PREVIOUS_PASSWORD);
  }

  public static UserException roleConflict() {
    return new UserException(ErrorCode.ROOT_ADMIN_ROLE_CONFLICT);
  }
}
