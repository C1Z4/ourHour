package com.ourhour.domain.board.service;

import com.ourhour.domain.board.dto.PostCreateUpdateReqDTO;
import com.ourhour.domain.board.dto.PostDTO;
import com.ourhour.domain.board.entity.BoardEntity;
import com.ourhour.domain.board.entity.PostEntity;
import com.ourhour.domain.board.mapper.PostMapper;
import com.ourhour.domain.board.repository.BoardRepository;
import com.ourhour.domain.board.repository.PostRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.util.UserContextHolder;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final PostMapper postMapper;

    public PageResponse<PostDTO> getAllPosts(Long orgId, Pageable pageable) {

        Page<PostEntity> postPage = postRepository.findAllByOrgId(orgId, pageable);

        if (postPage.isEmpty()) {
            return PageResponse.empty(pageable.getPageNumber(), pageable.getPageSize());
        }

        return PageResponse.of(postPage.map(postMapper::toDTO));
    }

    public PageResponse<PostDTO> getPostsByBoardId(Long orgId, Long boardId, Pageable pageable) {

        Page<PostEntity> postPage = postRepository.findPostsByBoardAndOrg(boardId, orgId, pageable);

        if (postPage.isEmpty()) {
            return PageResponse.empty(pageable.getPageNumber(), pageable.getPageSize());
        }

        return PageResponse.of(postPage.map(postMapper::toDTO));
    }

    public PostDTO getPostById(Long orgId, Long boardId, Long postId) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("게시글을 찾을 수 없습니다."));

        if (!post.getBoardEntity().getBoardId().equals(boardId)
                || !post.getBoardEntity().getOrgEntity().getOrgId().equals(orgId)) {
            throw BusinessException.forbidden("해당 게시글을 조회할 권한이 없습니다.");
        }

        return postMapper.toDTO(post);
    }

    @Transactional
    public void createPost(Long orgId, Long boardId, PostCreateUpdateReqDTO request) {

        Long currentMemberId = UserContextHolder.get().getOrgAuthorityList().stream()
                .filter(auth -> auth.getOrgId().equals(orgId))
                .map(auth -> auth.getMemberId())
                .findFirst()
                .orElseThrow(() -> BusinessException.unauthorized("작성자 정보를 찾을 수 없습니다."));

        MemberEntity author = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> BusinessException.notFound("작성자를 멤버 목록에서 찾을 수 없습니다."));

        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> BusinessException.notFound("게시판을 찾을 수 없습니다."));

        PostEntity newPost = PostEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .authorEntity(author)
                .boardEntity(board)
                .build();

        postRepository.save(newPost);
    }

    @Transactional
    public void updatePost(Long orgId, Long boardId, Long postId, PostCreateUpdateReqDTO request) {

        Long currentMemberId = getCurrentMemberId(orgId);

        PostEntity postToUpdate = postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("수정할 게시글을 찾을 수 없습니다."));

        if (!postToUpdate.getBoardEntity().getBoardId().equals(boardId)
                || !postToUpdate.getBoardEntity().getOrgEntity().getOrgId().equals(orgId)) {
            throw BusinessException.forbidden("요청 경로가 올바르지 않습니다.");
        }

        if (!postToUpdate.getAuthorEntity().getMemberId().equals(currentMemberId)) {
            throw BusinessException.forbidden("게시글을 수정할 권한이 없습니다.");
        }

        postToUpdate.update(request.getTitle(), request.getContent());
    }

    @Transactional
    public void deletePost(Long orgId, Long boardId, Long postId) {

        Long currentMemberId = getCurrentMemberId(orgId);

        PostEntity postToDelete = postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("삭제할 게시글을 찾을 수 없습니다."));

        if (!postToDelete.getBoardEntity().getBoardId().equals(boardId)
                || !postToDelete.getBoardEntity().getOrgEntity().getOrgId().equals(orgId)) {
            throw BusinessException.forbidden("요청 경로가 올바르지 않습니다.");
        }

        if (!postToDelete.getAuthorEntity().getMemberId().equals(currentMemberId)) {
            throw BusinessException.forbidden("게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(postToDelete);
    }

    private Long getCurrentMemberId(Long orgId) {
        Claims claims = UserContextHolder.get();
        if (claims == null) {
            throw BusinessException.unauthorized("인증 정보를 찾을 수 없습니다.");
        }

        return claims.getOrgAuthorityList().stream()
                .filter(auth -> auth.getOrgId().equals(orgId))
                .map(auth -> auth.getMemberId())
                .findFirst()
                .orElseThrow(() -> BusinessException.forbidden("멤버 정보를 찾을 수 없습니다."));
    }
}