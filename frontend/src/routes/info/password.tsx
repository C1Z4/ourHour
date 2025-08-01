import { useState } from 'react';

import { createFileRoute } from '@tanstack/react-router';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { PasswordChangeForm } from '@/components/member/PasswordChangeForm';
import { usePasswordUpdateMutation } from '@/hooks/queries/member/useMemberMutations';

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
    updatePassword({
      currentPassword: formData.currentPassword,
      newPassword: formData.newPassword,
      newPasswordCheck: formData.newPasswordCheck,
    });
    setFormData({
      currentPassword: '',
      newPassword: '',
      newPasswordCheck: '',
    });
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
