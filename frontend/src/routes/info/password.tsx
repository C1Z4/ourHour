import { useState } from 'react';

import { createFileRoute } from '@tanstack/react-router';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { PasswordChangeForm } from '@/components/member/PasswordChangeForm';
import { usePasswordUpdateMutation } from '@/hooks/queries/member/usePasswordUpdateMutation';
import { showSuccessToast } from '@/utils/toast';

export const Route = createFileRoute('/info/password')({
  component: PasswordPage,
});

function PasswordPage() {
  const { mutate: updatePassword } = usePasswordUpdateMutation();

  const [formData, setFormData] = useState<{
    currentPassword: string;
    newPassword: string;
    newPasswordCheck: string;
  }>({
    currentPassword: '',
    newPassword: '',
    newPasswordCheck: '',
  });

  const handleInputChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = () => {
    try {
      updatePassword({
        currentPassword: formData.currentPassword,
        newPassword: formData.newPassword,
        newPasswordCheck: formData.newPasswordCheck,
      });
      showSuccessToast('비밀번호가 변경되었습니다.');
    } catch (error) {
      // 에러 토스트
      // showErrorToast('비밀번호 변경 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="flex flex-col gap-6 justify-center items-center mt-20 p-20">
      <PasswordChangeForm formData={formData} onInputChange={handleInputChange} />
      <ButtonComponent onClick={handleSubmit} className="w-full max-w-lg" type="submit">
        비밀번호 변경
      </ButtonComponent>
    </div>
  );
}
