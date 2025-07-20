export const getImageUrl = (imageUrl: string | null): string => {
  if (!imageUrl) {
    return '';
  }

  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl;
  }

  // Base64 Data URL인 경우
  if (imageUrl.startsWith('data:')) {
    return imageUrl;
  }

  // 상대 경로인 경우 (예: /images/...)
  if (imageUrl.startsWith('/')) {
    const baseUrl = import.meta.env.DEV
      ? ''
      : import.meta.env.VITE_API_URL || 'http://localhost:8080';

    return `${baseUrl}${imageUrl}`;
  }

  return imageUrl;
};
