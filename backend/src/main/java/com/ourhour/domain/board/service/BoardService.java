package com.ourhour.domain.board.service;

import com.ourhour.domain.board.dto.BoardDTO;
import com.ourhour.domain.board.mapper.BoardMapper;
import com.ourhour.domain.board.dto.BoardResponseDTO;
import com.ourhour.domain.board.entity.BoardEntity;
import com.ourhour.domain.board.repository.BoardRepository;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.repository.OrgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    private final OrgRepository orgRepository;

    // 게시판 목록 조회
    public List<BoardResponseDTO> getBoardList(Long orgId) {

        List<BoardEntity> boardEntityList = boardRepository.findAllByOrgEntity_OrgId(orgId);

        List<BoardResponseDTO> boardResponseDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            BoardResponseDTO boardResponseDTO = boardMapper.toBoardResponseDTO(boardEntity);
            boardResponseDTOList.add(boardResponseDTO);
            System.out.println("boardResponseDTO.getBoardId() = " + boardResponseDTO.getBoardId());
            System.out.println("boardResponseDTO.getBoardId() = " + boardResponseDTO.getName());
        }

        return boardResponseDTOList;
    }


    public BoardResponseDTO createBoard(Long orgId, BoardDTO create) {
        OrgEntity orgEntity = orgRepository.findById(orgId).orElse(null);
        BoardEntity newBoard = BoardEntity.builder()
                .name(create.getName())
                .orgEntity(orgEntity)
                .build();

        boardRepository.save(newBoard);
        return boardMapper.toBoardResponseDTO(newBoard);
    }

    // 게시판 수정
    public BoardResponseDTO modifyBoard(Long boardId, BoardDTO request) {
        Optional<BoardEntity> optionalBoard = boardRepository.findById(boardId);
        if (optionalBoard.isPresent()) {
            BoardEntity boardEntity = optionalBoard.get();
            boardEntity.update(request.getName(), request.isFixed());
            BoardResponseDTO boardResponseDTO = boardMapper.toBoardResponseDTO(boardEntity);
            return boardResponseDTO;
        }
        return null;
    }

    // 게시판 삭제
    public void deleteBoard(Long boardId) {
        boolean isExists = boardRepository.existsById(boardId);
        if (isExists) {
            boardRepository.deleteById(boardId);
        }
    }


}

