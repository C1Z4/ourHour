import axios from 'axios';

import { axiosInstance } from '@/api/axiosConfig';
import { compressImageFile } from '@/utils/file/fileStorage';

export interface PresignRequest {
  fileName: string;
  contentType: string;
}

export interface PresignResponse {
  url: string;
  key: string;
  cdnUrl: string;
}

export const requestPresignedPutUrl = async (params: PresignRequest): Promise<PresignResponse> => {
  const { data } = await axiosInstance.post('/api/storage/presign', params);
  return data;
};

export const uploadToPresignedUrl = async (url: string, file: File, contentType: string) => {
  await axios.put(url, file, {
    headers: { 'Content-Type': contentType },
  });
};

export const resolveCdnUrlFromKey = (key: string) => {
  const cdnBase = (import.meta.env.VITE_CDN_URL as string | undefined)?.replace(/\/$/, '') || '';
  return cdnBase && key ? `${cdnBase}/${key.replace(/^\//, '')}` : key;
};

export const uploadImageWithCompression = async (
  file: File,
  maxWidth = 800,
  quality = 0.8,
): Promise<string> => {
  const compressedFile = await compressImageFile(file, maxWidth, quality);

  const { url, key } = await requestPresignedPutUrl({
    fileName: compressedFile.name,
    contentType: compressedFile.type,
  });

  await uploadToPresignedUrl(url, compressedFile, compressedFile.type);

  return resolveCdnUrlFromKey(key);
};
