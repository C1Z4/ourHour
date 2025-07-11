package com.ourhour.domain.board.service;

import com.ourhour.domain.board.dto.BoardDTO;
import java.util.List;

public interface BoardService {


    // 모든 게시판 조회
    List<BoardDTO> findAllBoard();

    // ID로 게시판 조회
    BoardDTO findBoardById(Long boardId);

    // 새 게시판 등록 (반환 타입을 BoardDTO로 변경)
    BoardDTO registBoard(BoardDTO boardDTO, Long orgId);

    // 게시판 정보 수정 (반환 타입을 BoardDTO로 변경)
    BoardDTO modifyBoard(BoardDTO boardDTO);

    // ID로 게시판 삭제
    void deleteBoard(Long boardId);


}
