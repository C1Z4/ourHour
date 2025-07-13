package com.ourhour.domain.comment.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.ourhour.domain.comment.dto.CommentDTO;
import com.ourhour.domain.comment.dto.CommentResDTO;
import com.ourhour.domain.comment.entity.CommentEntity;
import com.ourhour.domain.comment.dto.CommentUpdateReqDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    
    // CommentEntity 리스트 -> CommentResDTO
    default CommentResDTO toCommentResDTO(List<CommentEntity> commentEntities, Long postId, Long issueId) {
        if (commentEntities == null || commentEntities.isEmpty()) {
            return new CommentResDTO(postId, issueId, List.of());
        }
        
        // 댓글들을 parentCommentId 기준으로 그룹화
        Map<Long, List<CommentEntity>> childCommentMap = commentEntities.stream()
                .filter(comment -> comment.getParentCommentId() != null)
                .collect(Collectors.groupingBy(CommentEntity::getParentCommentId));
        
        // 최상위 댓글만 필터링하고 CommentDTO로 변환
        List<CommentDTO> topLevelComments = commentEntities.stream()
                .filter(comment -> comment.getParentCommentId() == null)
                .map(comment -> toCommentDTO(comment, childCommentMap))
                .collect(Collectors.toList());
        
        return new CommentResDTO(postId, issueId, topLevelComments);
    }
    
    // CommentEntity -> CommentDTO
    default CommentDTO toCommentDTO(CommentEntity commentEntity, Map<Long, List<CommentEntity>> childCommentMap) {
        if (commentEntity == null) {
            return null;
        }
        
        // 대댓글 조회
        List<CommentEntity> childEntities = childCommentMap.getOrDefault(commentEntity.getCommentId(), List.of());
        
        // 대댓글들을 재귀적으로 CommentDTO로 변환(빈 배열은 map이 실행되지 않음 -> 재귀 종료)
        List<CommentDTO> childComments = childEntities.stream()
                .map(child -> toCommentDTO(child, childCommentMap))
                .collect(Collectors.toList());
        
        return new CommentDTO(
            commentEntity.getCommentId(),
            commentEntity.getAuthorEntity().getMemberId(),
            commentEntity.getAuthorEntity().getName(),
            commentEntity.getAuthorEntity().getProfileImgUrl(),
            commentEntity.getContent(),
            commentEntity.getCreatedAt(),
            childComments
        );
    }

    // CommentUpdateReqDTO -> CommentEntity
    @Mapping(target = "postEntity", ignore = true)
    @Mapping(target = "issueEntity", ignore = true)
    @Mapping(target = "authorEntity", ignore = true)
    void updateCommentEntity(@MappingTarget CommentEntity commentEntity, CommentUpdateReqDTO commentUpdateReqDTO);
}
