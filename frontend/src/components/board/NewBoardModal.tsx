import { useState } from 'react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
  DialogClose,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { useBoardCreateMutation } from '@/hooks/queries/board/useBoardCreateMutation';
import { showErrorToast } from '@/utils/toast';

interface Props {
  orgId: number;
}

export const NewBoardModal = ({ orgId }: Props) => {
  const { mutate: createBoard } = useBoardCreateMutation(orgId);
  const [isOpen, setIsOpen] = useState(false);
  const [inputName, setInputName] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const savedName = inputName.trim();

    if (savedName === '') {
      showErrorToast('게시판 이름을 입력해주세요.');
      return;
    }

    createBoard(
      { name: savedName },
      {
        onSuccess: () => {
          setIsOpen(false);
          setInputName('');
        },
      },
    );
  };
  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <ButtonComponent>+ 게시판 추가</ButtonComponent>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>새 게시판 만들기</DialogTitle>
            <DialogDescription>
              새로운 게시판을 만들어 다양한 주제의 게시글을 작성해보세요!
            </DialogDescription>
          </DialogHeader>

          <div className="grid gap-4 py-4">
            <Input name={inputName} onChange={(e) => setInputName(e.target.value)} />
          </div>

          <DialogFooter>
            <DialogClose>
              {' '}
              <ButtonComponent variant="danger" onCanPlay={() => setIsOpen(false)}>
                취소
              </ButtonComponent>
            </DialogClose>
            <ButtonComponent type="submit">만들기</ButtonComponent>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};
