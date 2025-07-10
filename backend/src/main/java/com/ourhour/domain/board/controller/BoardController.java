package com.ourhour.domain.board.controller;

import com.ourhour.domain.board.dto.BoardDTO;
import com.ourhour.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController // @Controller 대신 @RestController 사용
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    /* 게시판 목록 조회 */
    @GetMapping
    public ResponseEntity<List<BoardDTO>> findAllBoards() {
        List<BoardDTO> boards = boardService.findAllBoard();
        return ResponseEntity.ok(boards);
    }

    /* 게시판 등록 */
    @PostMapping
    public ResponseEntity<Long> createBoard(@RequestBody BoardDTO boardDTO) {
        // 등록 시 OrgId는 실제 사용자 정보 등을 통해 가져와야 합니다. (예시: 1L)
        Long createdBoard = boardService.registBoard(boardDTO, 1L).getBoardId();
        return ResponseEntity.ok(createdBoard);
    }

    /* 게시판 수정 */
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardDTO> updateBoard(@PathVariable Long boardId, @RequestBody BoardDTO boardDTO) {
        boardDTO.setBoardId(boardId); // URL의 ID를 DTO에 설정
        BoardDTO updatedBoard = boardService.modifyBoard(boardDTO);
        return ResponseEntity.ok(updatedBoard);
    }

    /* 게시판 삭제  */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build(); // 성공적으로 삭제되었고, 별도 반환 데이터는 없음을 의미
    }


}
