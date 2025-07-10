package com.ourhour.domain.board.service;

import com.ourhour.domain.board.dto.BoardDTO;
import com.ourhour.domain.board.entity.BoardEntity;
import com.ourhour.domain.board.mapper.BoardMapper;
import com.ourhour.domain.board.repository.BoardRepository;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.repository.OrgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final OrgRepository orgRepository;
    private final BoardMapper boardMapper; // DTO 변환을 위해 Mapper 주입

    @Override
    public List<BoardDTO> findAllBoard() {
        List<BoardEntity> boardList = boardRepository.findAll();
        return boardMapper.toDTOList(boardList);
    }

    @Override
    public BoardDTO findBoardById(Long boardId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + boardId + "에 해당하는 게시판을 찾을 수 없습니다."));
        return boardMapper.toDTO(board);
    }

    @Override
    @Transactional
    public BoardDTO registBoard(BoardDTO boardDTO, Long orgId) {
        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + orgId + "에 해당하는 조직을 찾을 수 없습니다."));

        BoardEntity newBoard = BoardEntity.createBoard(boardDTO.getName(), boardDTO.isFixed(), orgEntity);
        boardRepository.save(newBoard);

        // 저장된 엔티티를 DTO로 변환하여 반환
        return boardMapper.toDTO(newBoard);
    }

    @Override
    @Transactional
    public BoardDTO modifyBoard(BoardDTO boardDTO) {
        BoardEntity board = boardRepository.findById(boardDTO.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("ID " + boardDTO.getBoardId() + "에 해당하는 게시판을 찾을 수 없습니다."));

        board.update(boardDTO.getName(), boardDTO.isFixed());

        // 수정된 엔티티를 DTO로 변환하여 반환
        return boardMapper.toDTO(board);
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }
}
