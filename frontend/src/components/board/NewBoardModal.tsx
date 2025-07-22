import { useState } from 'react';

import { useBoardCreateMutation } from '@/hooks/queries/board/useBoardCreateMutation';

import { BoardModal } from './BoardModal';
import { ButtonComponent } from '../common/ButtonComponent';
import { Input } from '../ui/input';

interface Props {
  orgId: number;
}

export const NewBoardModal = ({ orgId }: Props) => {
  const { mutate: createBoard } = useBoardCreateMutation(orgId);
  const [inputName, setInputName] = useState('');

  const handleConfirm = () => {
    createBoard(
      { name: inputName.trim() },
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
