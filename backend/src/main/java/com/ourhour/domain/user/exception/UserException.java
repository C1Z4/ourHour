package com.ourhour.domain.user.exception;

import com.ourhour.global.exception.BusinessException;

public class UserException extends BusinessException {
  public UserException(int status, String message) {
    super(status, message);
  }

  public static UserException invalidPwd() {
    return new UserException(400, "현재 비밀번호가 일치하지 않습니다.");
  }

  public static UserException notMatchPwd() {
    return new UserException(400, "새 비밀번호가 서로 일치하지 않습니다.");
  }

  public static UserException samePwd() {
    return new UserException(400, "이전 비밀번호와 동일합니다.");
  }

  public static UserException roleConflict() {
    return new UserException(403, "탈퇴하려면 다른 루트 관리자를 지정하거나 권한을 변경해야 합니다.");
  }
}
