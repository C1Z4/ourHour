import { useState, type ReactNode } from 'react';

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

import { ButtonComponent } from '../common/ButtonComponent';

interface Props {
  triggerButton: ReactNode;
  title: string;
  description: ReactNode;
  children?: ReactNode; // 별도로 들어갈 내용
  cancelButtonText?: string;
  confirmButtonText: string;
  onConfirm: () => void; // 확인 버튼을 눌렀을 때 실행될 함수
}

export const BoardModal = ({
  triggerButton,
  title,
  description,
  children,
  cancelButtonText = '취소',
  confirmButtonText,
  onConfirm,
}: Props) => {
  const [isOpen, setIsOpen] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onConfirm();
    setIsOpen(false);
  };

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>{triggerButton}</DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>{title}</DialogTitle>
            <DialogDescription>{description}</DialogDescription>
          </DialogHeader>

          {/* 별도로 추가할 내용 */}
          {children}

          <DialogFooter>
            <DialogClose asChild>
              <ButtonComponent variant="danger">{cancelButtonText}</ButtonComponent>
            </DialogClose>
            <ButtonComponent type="submit">{confirmButtonText}</ButtonComponent>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};
