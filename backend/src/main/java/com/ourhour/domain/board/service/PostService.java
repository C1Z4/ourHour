package com.ourhour.domain.board.service;

import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.board.dto.PostCreateUpdateReqDTO;
import com.ourhour.domain.board.dto.PostDTO;
import com.ourhour.domain.board.entity.BoardEntity;
import com.ourhour.domain.board.entity.PostEntity;
import com.ourhour.domain.board.exception.BoardException;
import com.ourhour.domain.board.exception.PostException;
import com.ourhour.domain.board.mapper.PostMapper;
import com.ourhour.domain.board.repository.BoardRepository;
import com.ourhour.domain.board.repository.PostRepository;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.global.common.dto.PageResponse;
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
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;

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
                .orElseThrow(() -> PostException.postNotFoundException());

        if (!post.getBoardEntity().getBoardId().equals(boardId)
                || !post.getBoardEntity().getOrgEntity().getOrgId().equals(orgId)) {
            throw PostException.postAccessDeniedException();
        }

        return postMapper.toDTO(post);
    }

    @Transactional
    public PostDTO createPost(Long orgId, Long boardId, PostCreateUpdateReqDTO request) {

        Long currentMemberId = UserContextHolder.get().getOrgAuthorityList().stream()
                .filter(auth -> auth.getOrgId().equals(orgId))
                .map(auth -> auth.getMemberId())
                .findFirst()
                .orElseThrow(() -> PostException.postAuthorNotFoundException());

        MemberEntity author = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> PostException.postAuthorNotFoundException());

        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> BoardException.boardNotFoundException());

        PostEntity newPost = PostEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .authorEntity(author)
                .boardEntity(board)
                .build();

        postRepository.save(newPost);

        return postMapper.toDTO(newPost);
    }

    @Transactional
    public void updatePost(Long orgId, Long boardId, Long postId, PostCreateUpdateReqDTO request) {

        Long currentMemberId = getCurrentMemberId(orgId);

        PostEntity postToUpdate = postRepository.findById(postId)
                .orElseThrow(() -> PostException.postNotFoundException());

        if (!postToUpdate.getBoardEntity().getBoardId().equals(boardId)
                || !postToUpdate.getBoardEntity().getOrgEntity().getOrgId().equals(orgId)) {
            throw PostException.postAccessDeniedException();
        }

        if (!postToUpdate.getAuthorEntity().getMemberId().equals(currentMemberId)) {
            throw PostException.postUpdateAccessDeniedException();
        }

        postToUpdate.update(request.getTitle(), request.getContent());
    }

    @Transactional
    public void deletePost(Long orgId, Long boardId, Long postId) {

        Long currentMemberId = getCurrentMemberId(orgId);

        PostEntity postToDelete = postRepository.findById(postId)
                .orElseThrow(() -> PostException.postNotFoundException());

        if (!postToDelete.getBoardEntity().getBoardId().equals(boardId)
                || !postToDelete.getBoardEntity().getOrgEntity().getOrgId().equals(orgId)) {
            throw PostException.postAuthorAccessDeniedException();
        }

        if (!canDeletePost(orgId, postToDelete, currentMemberId)) {
            throw PostException.postAuthorAccessDeniedException();
        }

        postRepository.delete(postToDelete);
    }

    private boolean canDeletePost(Long orgId, PostEntity postEntity, Long currentMemberId) {
        // 본인이 작성한 게시글인 경우
        if (postEntity.getAuthorEntity().getMemberId().equals(currentMemberId)) {
            return true;
        }

        // 현재 사용자의 해당 조직에서의 권한 확인
        return orgParticipantMemberRepository
                .findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId, currentMemberId, Status.ACTIVE)
                .map(opm -> opm.getRole().equals(Role.ADMIN) || opm.getRole().equals(Role.ROOT_ADMIN))
                .orElse(false);
    }

    private Long getCurrentMemberId(Long orgId) {
        Claims claims = UserContextHolder.get();
        if (claims == null) {
            throw AuthException.unauthorizedException();
        }

        return claims.getOrgAuthorityList().stream()
                .filter(auth -> auth.getOrgId().equals(orgId))
                .map(auth -> auth.getMemberId())
                .findFirst()
                .orElseThrow(() -> PostException.postAuthorNotFoundException());
    }
}