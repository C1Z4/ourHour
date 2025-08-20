export const formatPhoneNumber = (value: string): string => {
  // 숫자만 추출
  const numbers = value.replace(/[^0-9]/g, '');

  // 11자리 이하로 제한
  const limitedNumbers = numbers.slice(0, 11);

  // 길이에 따라 하이픈 추가
  if (limitedNumbers.length <= 3) {
    return limitedNumbers;
  }

  if (limitedNumbers.length <= 7) {
    return `${limitedNumbers.slice(0, 3)}-${limitedNumbers.slice(3)}`;
  }

  return `${limitedNumbers.slice(0, 3)}-${limitedNumbers.slice(3, 7)}-${limitedNumbers.slice(7)}`;
};

export const handlePhoneInputChange = (
  value: string,
  onInputChange: (field: string, value: string) => void,
  fieldName: string,
): void => {
  const formattedValue = formatPhoneNumber(value);
  onInputChange(fieldName, formattedValue);
};
