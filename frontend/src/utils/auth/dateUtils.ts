// ISO 문자열을 한국 시간 기준으로 포맷팅
export const formatIsoToDate = (isoString: string) => {
  if (!isoString) {
    return '';
  }

  const date = new Date(isoString);

  // 유효하지 않은 날짜인지 확인
  if (isNaN(date.getTime())) {
    return '';
  }

  const today = new Date();

  // 날짜만 비교 (시간 제외)
  const isToday =
    date.getFullYear() === today.getFullYear() &&
    date.getMonth() === today.getMonth() &&
    date.getDate() === today.getDate();

  if (isToday) {
    // 오늘인 경우 시간 포맷 (HH:mm)
    return date.toLocaleTimeString('ko-KR', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false,
    });
  }
  // 오늘이 아닌 경우 날짜 포맷 (YYYY-MM-DD)
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// 날짜만 포맷팅하는 함수
export const formatIsoToDateOnly = (isoString: string) => {
  if (!isoString) {
    return '';
  }

  const date = new Date(isoString);

  if (isNaN(date.getTime())) {
    return '';
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};
