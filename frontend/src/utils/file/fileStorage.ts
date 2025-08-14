export const compressAndSaveImage = async (
  file: File,
  maxWidth = 800,
  quality = 0.8,
): Promise<string> =>
  new Promise((resolve, reject) => {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    const img = new Image();

    img.onload = () => {
      // 이미지 크기 조정
      const { width, height } = img;
      let newWidth = width;
      let newHeight = height;

      if (width > maxWidth) {
        newWidth = maxWidth;
        newHeight = (height * maxWidth) / width;
      }

      canvas.width = newWidth;
      canvas.height = newHeight;

      // 이미지 그리기
      ctx?.drawImage(img, 0, 0, newWidth, newHeight);

      // 압축된 이미지 URL 생성
      const compressedDataUrl = canvas.toDataURL('image/jpeg', quality);
      resolve(compressedDataUrl);
    };

    img.onerror = () => {
      reject(new Error('이미지 로드 실패'));
    };

    img.src = URL.createObjectURL(file);
  });

export const compressImageFile = async (file: File, maxWidth = 800, quality = 0.8): Promise<File> =>
  new Promise((resolve, reject) => {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    const img = new Image();

    img.onload = () => {
      // 이미지 크기 조정
      const { width, height } = img;
      let newWidth = width;
      let newHeight = height;

      if (width > maxWidth) {
        newWidth = maxWidth;
        newHeight = (height * maxWidth) / width;
      }

      canvas.width = newWidth;
      canvas.height = newHeight;

      // 이미지 그리기
      ctx?.drawImage(img, 0, 0, newWidth, newHeight);

      // 압축된 파일 생성
      canvas.toBlob(
        (blob) => {
          if (blob) {
            const compressedFile = new File([blob], file.name, {
              type: 'image/jpeg',
              lastModified: Date.now(),
            });
            resolve(compressedFile);
          } else {
            reject(new Error('이미지 압축 실패'));
          }
        },
        'image/jpeg',
        quality,
      );
    };

    img.onerror = () => {
      reject(new Error('이미지 로드 실패'));
    };

    img.src = URL.createObjectURL(file);
  });

export const validateFileSize = (file: File, maxSizeMB = 5): boolean => {
  const maxSizeBytes = maxSizeMB * 1024 * 1024;
  return file.size <= maxSizeBytes;
};

export const validateFileType = (
  file: File,
  allowedTypes: string[] = ['image/jpeg', 'image/png', 'image/gif'],
): boolean => allowedTypes.includes(file.type);
