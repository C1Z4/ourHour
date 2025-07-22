import { useState } from 'react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Input } from '@/components/ui/input';
import { useBoardCreateMutation } from '@/hooks/queries/board/useBoardCreateMutation';
import { showErrorToast } from '@/utils/toast';

import { BoardModal } from './BoardModal';

interface Props {
  orgId: number;
}

export const NewBoardModal = ({ orgId }: Props) => {
  const { mutate: createBoard } = useBoardCreateMutation(orgId);
  const [inputName, setInputName] = useState('');

  const handleConfirm = () => {
    const savedName = inputName.trim();

    if (savedName === '') {
      showErrorToast('게시판 이름을 입력해주세요.');
      return;
    }

    createBoard(
      { name: savedName },
      {
        onSuccess: () => setInputName(''),
      },
    );
  };

  return (
    <BoardModal
      triggerButton={<ButtonComponent>+ 게시판 추가</ButtonComponent>}
      title="새 게시판 만들기"
      description="새로운 게시판을 만들어 다양한 주제의 게시글을 작성해보세요!"
      confirmButtonText="만들기"
      onConfirm={handleConfirm}
    >
      <div className="grid gap-4 py-4">
        <Input value={inputName} onChange={(e) => setInputName(e.target.value)} />
      </div>
    </BoardModal>
  );
};
