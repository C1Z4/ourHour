package com.ourhour.domain.board.controller;

import com.ourhour.domain.board.dto.BoardDTO;
import com.ourhour.domain.board.dto.BoardResponseDTO;
import com.ourhour.domain.board.service.BoardService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.jwt.annotation.OrgId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //@OrgAuth
    //게시판 전체 목록 조회
    @GetMapping("/{orgId}/boards")
    public ResponseEntity<ApiResponse<List<BoardResponseDTO>>> getBoardList(@OrgId @PathVariable Long orgId) {

        List<BoardResponseDTO> boardResponseDTOList = boardService.getBoardList(orgId);

        ApiResponse<List<BoardResponseDTO>> apiResponse = ApiResponse.success(boardResponseDTOList);

        return ResponseEntity.ok(apiResponse);

    }

    // 게시판 등록
    @PostMapping("/{orgId}/boards" )
    public ResponseEntity<ApiResponse<BoardResponseDTO>> createBoard(@OrgId @PathVariable Long orgId, @RequestBody BoardDTO create) {

        BoardResponseDTO newBoard = boardService.createBoard(orgId, create);
        return ResponseEntity.ok(ApiResponse.success(newBoard));
    }

   //게시판 수정
    @PutMapping("/{orgId}/boards/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponseDTO>> modifyBoard(@OrgId @PathVariable Long orgId, @PathVariable Long boardId, @RequestBody BoardDTO modify) {
        BoardResponseDTO boardResponseDTO = boardService.modifyBoard(boardId, modify);
        return ResponseEntity.ok(ApiResponse.success(boardResponseDTO));
    }

  // 게시판 삭제
    @DeleteMapping("/{orgId}/boards/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@OrgId @PathVariable Long orgId, @PathVariable Long boardId) {

        boardService.deleteBoard(boardId);

        return ResponseEntity.ok(ApiResponse.success(null));

    }


}
