import { useState } from 'react';

import { useBoardUpdateMutation } from '@/hooks/queries/board/useBoardUpdateMutation';

import { BoardModal } from './BoardModal';
import { ButtonComponent } from '../common/ButtonComponent';
import { Input } from '../ui/input';

interface Props {
  orgId: number;
  boardId: number;
}

export const UpdateBoardModal = ({ orgId, boardId }: Props) => {
  const { mutate: updateBoard } = useBoardUpdateMutation(orgId, boardId);
  const [inputName, setInputName] = useState('');

  const handleConfirm = () => {
    updateBoard(inputName.trim(), {
      onSuccess: () => setInputName(''),
    });
  };

  return (
    <BoardModal
      triggerButton={<ButtonComponent>게시판 이름 수정</ButtonComponent>}
      title="게시판 이름 수정"
      description="새로운 게시판 이름을 입력해주세요"
      confirmButtonText="수정하기"
      onConfirm={handleConfirm}
    >
      <div className="grid gap-4 py-4">
        <Input value={inputName} onChange={(e) => setInputName(e.target.value)} />
      </div>
    </BoardModal>
  );
};
