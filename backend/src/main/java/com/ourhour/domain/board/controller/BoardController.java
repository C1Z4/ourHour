package com.ourhour.domain.board.controller;

import com.ourhour.domain.board.dto.BoardDTO;
import com.ourhour.domain.board.dto.BoardResponseDTO;
import com.ourhour.domain.board.service.BoardService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.jwt.annotation.OrgId;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.domain.org.enums.Role;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
@Tag(name = "게시판", description = "조직 내 게시판 관리 API")
public class BoardController {

    private final BoardService boardService;

    // 게시판 전체 목록 조회
    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{orgId}/boards")
    @Operation(summary = "게시판 목록 조회", description = "조직 내 게시판 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<BoardResponseDTO>>> getBoardList(@OrgId @PathVariable Long orgId) {

        List<BoardResponseDTO> boardResponseDTOList = boardService.getBoardList(orgId);

        ApiResponse<List<BoardResponseDTO>> apiResponse = ApiResponse.success(boardResponseDTOList);

        return ResponseEntity.ok(apiResponse);

    }

    // 게시판 등록
    @OrgAuth(accessLevel = Role.ADMIN)
    @PostMapping("/{orgId}/boards")
    @Operation(summary = "게시판 등록", description = "새 게시판을 생성합니다.")
    public ResponseEntity<ApiResponse<BoardResponseDTO>> createBoard(@OrgId @PathVariable Long orgId,
            @RequestBody BoardDTO create) {

        BoardResponseDTO newBoard = boardService.createBoard(orgId, create);
        return ResponseEntity.ok(ApiResponse.success(newBoard));
    }

    // 게시판 수정
    @OrgAuth(accessLevel = Role.ADMIN)
    @PutMapping("/{orgId}/boards/{boardId}")
    @Operation(summary = "게시판 수정", description = "게시판 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<BoardResponseDTO>> modifyBoard(@OrgId @PathVariable Long orgId,
            @PathVariable Long boardId, @RequestBody BoardDTO modify) {
        BoardResponseDTO boardResponseDTO = boardService.modifyBoard(boardId, modify);
        return ResponseEntity.ok(ApiResponse.success(boardResponseDTO));
    }

    // 게시판 삭제
    @OrgAuth(accessLevel = Role.ADMIN)
    @DeleteMapping("/{orgId}/boards/{boardId}")
    @Operation(summary = "게시판 삭제", description = "게시판을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@OrgId @PathVariable Long orgId, @PathVariable Long boardId) {

        boardService.deleteBoard(boardId);

        return ResponseEntity.ok(ApiResponse.success(null));

    }

}
