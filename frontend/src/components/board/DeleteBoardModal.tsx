import { BoardModal } from '@/components/board/BoardModal';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { useBoardDeleteMutation } from '@/hooks/queries/board/useBoardMutations';

interface Props {
  orgId: number;
  boardId: number;
}

export const DeleteBoardModal = ({ orgId, boardId }: Props) => {
  const { mutate: deleteBoard } = useBoardDeleteMutation(orgId, boardId);

  const handleConfirm = () => {
    deleteBoard();
  };

  return (
    <BoardModal
      triggerButton={<ButtonComponent variant="danger">게시판 삭제</ButtonComponent>}
      title="게시판을 삭제하시겠습니까?"
      description="해당 게시판의 모든 데이터가 삭제되며, 이 작업은 되돌릴 수 없습니다."
      confirmButtonText="삭제"
      onConfirm={handleConfirm}
    />
  );
};
