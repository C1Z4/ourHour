package com.ourhour.domain.board.controller;

import com.ourhour.domain.board.dto.PostCreateUpdateReqDTO;
import com.ourhour.domain.board.dto.PostDTO;
import com.ourhour.domain.board.service.PostService;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organizations")
public class PostController {

    private final PostService postService;

    // 전체 게시글 조회(게시판 구분 x)
    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{orgId}/boards/posts")
    public ResponseEntity<ApiResponse<PageResponse<PostDTO>>> getAllPosts(
            @OrgId @PathVariable Long orgId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "postId"));

        PageResponse<PostDTO> postPage = postService.getAllPosts(orgId, pageable);

        return ResponseEntity.ok(ApiResponse.success(postPage, "게시글 전체 조회에 성공했습니다."));

    }

    // 게시판 별 게시글 조회
    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{orgId}/boards/{boardId}/posts")
    public ResponseEntity<ApiResponse<PageResponse<PostDTO>>> getPostsByBoardId(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "postId"));

        PageResponse<PostDTO> postPage = postService.getPostsByBoardId(orgId, boardId, pageable);

        return ResponseEntity.ok(ApiResponse.success(postPage, "게시판 별 게시글 전체 조회에 성공했습니다."));
    }

    // 게시글 상세 조회
    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{orgId}/boards/{boardId}/posts/{postId}")
    public ResponseEntity<ApiResponse<PostDTO>> getPostById(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long boardId,
            @PathVariable Long postId) {

        PostDTO post = postService.getPostById(orgId, boardId, postId);

        return ResponseEntity.ok(ApiResponse.success(post, "게시글 상세 조회에 성공했습니다."));
    }

    // 게시글 등록
    @OrgAuth(accessLevel = Role.MEMBER)
    @PostMapping("/{orgId}/boards/{boardId}/posts")
    public ResponseEntity<ApiResponse<Void>> registPost(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long boardId,
            @RequestBody PostCreateUpdateReqDTO request) {

        postService.createPost(orgId, boardId, request);

        return ResponseEntity.ok(ApiResponse.success(null, "게시글 등록에 성공했습니다."));
    }

    // 게시글 수정
    @OrgAuth(accessLevel = Role.MEMBER)
    @PutMapping("/{orgId}/boards/{boardId}/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> modifyPost(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @RequestBody PostCreateUpdateReqDTO request) {

        postService.updatePost(orgId, boardId, postId, request);

        return ResponseEntity.ok(ApiResponse.success(null, "게시글 수정에 성공했습니다."));
    }

    // 게시글 삭제
    @OrgAuth(accessLevel = Role.MEMBER)
    @DeleteMapping("/{orgId}/boards/{boardId}/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long boardId,
            @PathVariable Long postId) {

        postService.deletePost(orgId, boardId, postId);

        return ResponseEntity.ok(ApiResponse.success(null, "게시글 삭제에 성공했습니다."));
    }
}