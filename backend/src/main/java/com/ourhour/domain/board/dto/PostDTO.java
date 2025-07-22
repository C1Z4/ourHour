package com.ourhour.domain.board.dto;

import com.ourhour.domain.board.entity.PostEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostDTO {

    /*
     * 게시글의 고유 식별자(ID).
     * - 응답(Response) 시에는 항상 값이 채워집니다.
     * - 생성 요청(Request) 시에는 값이 비어있습니다.
     */
    private Long postId;

    /**
     * 게시글이 속한 게시판의 ID.
     * - 생성 요청(Request) 시에는 이 필드가 사용되지 않고, URL 경로의 boardId를 사용합니다.
     * - 응답(Response) 시에는 게시글이 속한 게시판의 ID를 포함합니다.
     */
    private Long boardId;

    /**
     * 게시글이 속한 게시판의 이름.
     * - 응답(Response) 시에만 사용됩니다.
     */
    private String boardName;

    /**
     * 게시글 작성자의 ID.
     * - 응답(Response) 시에만 사용됩니다.
     */
    private Long authorId;

    /**
     * 게시글 작성자의 이름.
     * - 응답(Response) 시에만 사용됩니다.
     */
    private String authorName;

    /**
     * 게시글 작성자의 프로필 이미지.
     * - 응답(Response) 시에만 사용됩니다.
     */
    private String authorProfileImgUrl;

    /**
     * 게시글의 제목.
     */
    private String title;

    /**
     * 게시글의 내용.
     */
    private String content;

    /**
     * 게시글 생성 일시.
     * - 응답(Response) 시에만 사용됩니다.
     */
    private LocalDateTime createdAt;

    /**
     * PostEntity 객체를 받아 DTO 객체를 생성하는 생성자.
     * 주로 서비스 계층에서 엔티티를 DTO로 변환하여 컨트롤러에 반환할 때 사용됩니다.
     * 
     * @param entity 데이터베이스에서 조회한 PostEntity 객체
     */
    public PostDTO(PostEntity entity) {
        this.postId = entity.getPostId();
        // BoardEntity가 null이 아닐 경우에만 boardId를 가져옵니다.
        if (entity.getBoardEntity() != null) {
            this.boardId = entity.getBoardEntity().getBoardId();
        }
        if (entity.getBoardEntity() != null) {
            this.boardName = entity.getBoardEntity().getName();
        }
        // MemberEntity가 null이 아닐 경우에만 작성자 이름을 가져옵니다. (탈퇴한 회원 등)
        if (entity.getAuthorEntity() != null) {
            this.authorId = entity.getAuthorEntity().getMemberId();
        }
        if (entity.getAuthorEntity() != null) {
            this.authorName = entity.getAuthorEntity().getName(); // MemberEntity에 getName() 메소드가 있다고 가정
        } else {
            this.authorName = "알 수 없음";
        }
        if (entity.getAuthorEntity() != null) {
            this.authorProfileImgUrl = entity.getAuthorEntity().getProfileImgUrl();
        }
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.createdAt = entity.getCreatedAt();
    }
}
