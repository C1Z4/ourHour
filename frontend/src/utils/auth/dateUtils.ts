//  ISO 문자열 -> YYYY-MM-DD HH:mm
export const formatIsoToDate = (isoString: string) => {
  if (!isoString) {
    return '';
  }
  const dateTime = isoString.replace('T', ' ');
  return dateTime.slice(0, 10);
};
