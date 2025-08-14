import { ButtonComponent } from '@/components/common/ButtonComponent';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  isPending: boolean;
}

export const ChatRoomExitAlert = ({ isOpen, onClose, onConfirm, isPending }: Props) => (
  <AlertDialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
    <AlertDialogContent>
      <AlertDialogHeader>
        <AlertDialogTitle>정말 나가시겠습니까?</AlertDialogTitle>
        <AlertDialogDescription>
          해당 채팅방과 관련된 모든 정보가 삭제되며, 이 작업은 되돌릴 수 없습니다.
        </AlertDialogDescription>
      </AlertDialogHeader>
      <AlertDialogFooter>
        <AlertDialogAction asChild>
          <ButtonComponent variant="danger" onClick={onConfirm} disabled={isPending}>
            {isPending ? '나가는 중...' : '나가기'}
          </ButtonComponent>
        </AlertDialogAction>
        <AlertDialogCancel asChild>
          <ButtonComponent variant="primary">취소</ButtonComponent>
        </AlertDialogCancel>
      </AlertDialogFooter>
    </AlertDialogContent>
  </AlertDialog>
);
