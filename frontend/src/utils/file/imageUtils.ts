export const getImageUrl = (imageUrl: string | null): string => {
  if (!imageUrl) {
    return '';
  }
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl;
  }
  if (imageUrl.startsWith('data:')) {
    return imageUrl;
  }

  const cdnBase = import.meta.env.VITE_CDN_URL as string | undefined;
  if (cdnBase) {
    const base = cdnBase.replace(/\/$/, '');
    const path = imageUrl.startsWith('/') ? imageUrl.slice(1) : imageUrl;
    return `${base}/${path}`;
  }

  const baseUrl = import.meta.env.DEV
    ? ''
    : import.meta.env.VITE_API_URL || 'http://localhost:8080';
  return imageUrl.startsWith('/') ? `${baseUrl}${imageUrl}` : imageUrl;
};
