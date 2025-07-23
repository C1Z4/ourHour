import { useState } from 'react';

import getCheckEmail from '@/api/auth/getCheckEmail';
import postPasswordVerification from '@/api/auth/postPwdVerification';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

import { PwdResetSendVerification } from './PwdResetSendVerification';

export function PwdResetContainer() {
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (email: string) => {
    setLoading(true);
    try {
      const isNotRegistered = await getCheckEmail({ email });

      if (isNotRegistered.data) {
        showErrorToast('등록되지 않은 이메일입니다.');
        throw new Error('Unregistered email');
      }

      await postPasswordVerification({ email });
      showSuccessToast('비밀번호 재설정 링크가 이메일로 발송되었습니다.');
    } catch (error) {
      if (!(error instanceof Error && error.message === 'Unregistered email')) {
        showErrorToast('메일 발송 중 오류가 발생했습니다.');
      }
    } finally {
      setLoading(false);
    }
  };

  return <PwdResetSendVerification onSubmit={handleSubmit} isLoading={loading} />;
}
