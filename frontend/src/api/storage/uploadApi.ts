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
  const result = cdnBase && key ? `${cdnBase}/${key.replace(/^\//, '')}` : key;
  return result;
};

export const uploadImageWithCompression = async (
  file: File,
  maxWidth = 800,
  quality = 0.8,
): Promise<string> => {
  const compressedFile = await compressImageFile(file, maxWidth, quality);

  const presignResponse = await requestPresignedPutUrl({
    fileName: compressedFile.name,
    contentType: compressedFile.type,
  });

  const { url, key, cdnUrl } = presignResponse;

  await uploadToPresignedUrl(url, compressedFile, compressedFile.type);

  // 백엔드에서 제공하는 cdnUrl을 우선 사용, 없으면 key로 구성
  const finalUrl = cdnUrl || resolveCdnUrlFromKey(key);

  if (!finalUrl || finalUrl.trim() === '') {
    throw new Error('CDN URL이 생성되지 않았습니다');
  }

  return finalUrl;
};

export interface DeleteImageRequest {
  imageUrl: string;
}

export interface DeleteImageResponse {
  status: string;
  message: string;
  data: null;
}

export const deleteImage = async (imageUrl: string): Promise<DeleteImageResponse> => {
  const { data } = await axiosInstance.delete('/api/storage/images', {
    data: { imageUrl },
  });
  return data;
};
