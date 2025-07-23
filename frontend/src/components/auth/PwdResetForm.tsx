import { useState } from 'react';

import { useRouter } from '@tanstack/react-router';

import patchPwdReset from '@/api/auth/patchPwdReset';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Route as successRoute } from '@/routes/auth/password/success';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

import { ModalComponent } from '../common/ModalComponent';

export function PwdResetForm() {
  const router = useRouter();
  const { token } = successRoute.useSearch();
  const [pwd, setPwd] = useState('');
  const [pwdCheck, setPwdCheck] = useState('');
  const [err, setErr] = useState('');
  const [loading, setLoading] = useState(false);

  const validatePassword = (password: string) => {
    const hasLower = /[a-z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const isLengthValid = password.length >= 8;
    return { hasLower, hasNumber, isLengthValid, isValid: hasLower && hasNumber && isLengthValid };
  };

  const pwdValidation = validatePassword(pwd);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const { isValid } = validatePassword(pwd);

    if (!pwd || !pwdCheck) {
      setErr('비밀번호를 모두 입력해주세요.');
      return;
    }
    if (!isValid) {
      setErr('비밀번호가 규칙을 만족하지 않습니다.');
      return;
    }
    if (pwd !== pwdCheck) {
      setErr('비밀번호가 일치하지 않습니다.');
      return;
    }

    try {
      setLoading(true);
      setErr('');
      await patchPwdReset({ token: token ?? '', newPassword: pwd, newPasswordCheck: pwdCheck });
      showSuccessToast('비밀번호가 변경되었습니다.');
      router.navigate({ to: '/login' });
    } catch {
      showErrorToast('비밀번호 변경에 실패했습니다. 다시 시도해주세요.');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    router.navigate({ to: '/login' });
  };

  return (
    <ModalComponent
      isOpen={true}
      onClose={handleClose}
      title="비밀번호 재설정"
      size="xl"
      footer={
        <div className="flex justify-center w-full">
          {/* footer에서 form submit 트리거 */}
          <ButtonComponent variant="primary" type="submit" form="pwdResetForm" disabled={loading}>
            {loading ? '변경 중...' : '비밀번호 변경'}
          </ButtonComponent>
        </div>
      }
    >
      {/* NOTE: footer 버튼이 form="pwdResetForm" 를 지정하므로 여기 form id 필수 */}
      <form id="pwdResetForm" onSubmit={handleSubmit} className="flex flex-col gap-4">
        <div>
          <label htmlFor="pwd" className="block text-sm font-medium mb-1">
            새 비밀번호
          </label>
          <input
            id="pwd"
            type="password"
            value={pwd}
            onChange={(e) => setPwd(e.target.value)}
            className="border border-gray-300 rounded-md p-2 w-full text-sm"
            placeholder="새 비밀번호를 입력하세요"
            autoComplete="new-password"
          />
        </div>

        <div className="text-xs text-gray-600 space-y-1">
          <p className={pwdValidation.hasLower ? 'text-green-600' : 'text-gray-500'}>
            • 소문자 1자 이상 포함
          </p>
          <p className={pwdValidation.hasNumber ? 'text-green-600' : 'text-gray-500'}>
            • 숫자 1자 이상 포함
          </p>
          <p className={pwdValidation.isLengthValid ? 'text-green-600' : 'text-gray-500'}>
            • 최소 8자리 이상
          </p>
        </div>

        <div>
          <label htmlFor="pwdCheck" className="block text-sm font-medium mb-1">
            비밀번호 확인
          </label>
          <input
            id="pwdCheck"
            type="password"
            value={pwdCheck}
            onChange={(e) => setPwdCheck(e.target.value)}
            className="border border-gray-300 rounded-md p-2 w-full text-sm"
            placeholder="비밀번호를 다시 입력하세요"
            autoComplete="new-password"
          />
        </div>

        {err && <p className="text-xs text-red-500">{err}</p>}
      </form>
    </ModalComponent>
  );
}
