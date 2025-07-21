package com.ourhour.domain.board.service;

import com.ourhour.domain.board.dto.PostDTO;
import com.ourhour.domain.board.entity.BoardEntity;
import com.ourhour.domain.board.entity.PostEntity;
import com.ourhour.domain.board.exceptions.PostException;
import com.ourhour.domain.board.mapper.PostMapper;
import com.ourhour.domain.board.repository.BoardRepository;
import com.ourhour.domain.board.repository.PostRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final PostMapper postMapper;

    public PostDTO createPost(Long boardId, PostDTO requestDTO) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다."));

        PostEntity newPost = PostEntity.builder()
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .boardEntity(board)
                .build();

        PostEntity savedPost = postRepository.save(newPost);
        return postMapper.toDTO(savedPost);
    }

    public List<PostDTO> findPostsByBoard(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new IllegalArgumentException("게시판을 찾을 수 없습니다.");
        }
        List<PostEntity> posts = postRepository.findByBoardEntity_BoardIdOrderByPostIdDesc(boardId);
        return posts.stream()
                .map(postMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PostDTO findPostById(Long postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(postId));
        return postMapper.toDTO(post);
    }


    public PostDTO updatePost(Long postId, PostDTO requestDTO) {
        PostEntity existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(postId));

        existingPost.update(requestDTO.getTitle(), requestDTO.getContent());
        return postMapper.toDTO(existingPost);
    }


    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostException(postId);
        }
        postRepository.deleteById(postId);
    }
}