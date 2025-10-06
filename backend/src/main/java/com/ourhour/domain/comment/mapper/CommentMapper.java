package com.ourhour.domain.comment.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.ourhour.domain.comment.service.CommentLikeService;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

        // CommentEntity 리스트 -> CommentResDTO (N+1 최적화 버전)
        default CommentResDTO toCommentResDTO(List<CommentEntity> commentEntities, Long postId, Long issueId,
                        CommentLikeService commentLikeService, Long currentMemberId) {
                if (commentEntities == null || commentEntities.isEmpty()) {
                        return new CommentResDTO(postId, issueId, List.of());
                }

                // 모든 댓글 ID 수집
                List<Long> allCommentIds = commentEntities.stream()
                                .map(CommentEntity::getCommentId)
                                .collect(Collectors.toList());

                // 한번에 좋아요 수 조회 (N+1 방지)
                Map<Long, Long> likeCountMap = commentLikeService.getLikeCountsMap(allCommentIds);

                // 현재 사용자의 좋아요 여부 한번에 조회 (N+1 방지)
                Set<Long> likedCommentIds = currentMemberId != null ?
                                commentLikeService.getLikedCommentIds(currentMemberId, allCommentIds) : Set.of();

                // 댓글들을 parentCommentId 기준으로 그룹화
                Map<Long, List<CommentEntity>> childCommentMap = commentEntities.stream()
                                .filter(comment -> comment.getParentCommentId() != null)
                                .collect(Collectors.groupingBy(CommentEntity::getParentCommentId));

                // 최상위 댓글만 필터링하고 CommentDTO로 변환
                List<CommentDTO> topLevelComments = commentEntities.stream()
                                .filter(comment -> comment.getParentCommentId() == null)
                                .map(comment -> toCommentDTO(comment, childCommentMap, likeCountMap, likedCommentIds))
                                .collect(Collectors.toList());

                return new CommentResDTO(postId, issueId, topLevelComments);
        }

        // CommentEntity -> CommentDTO (N+1 최적화 버전)
        default CommentDTO toCommentDTO(CommentEntity commentEntity, Map<Long, List<CommentEntity>> childCommentMap,
                        Map<Long, Long> likeCountMap, Set<Long> likedCommentIds) {
                if (commentEntity == null) {
                        return null;
                }

                // 대댓글 조회
                List<CommentEntity> childEntities = childCommentMap.getOrDefault(commentEntity.getCommentId(),
                                List.of());

                // 대댓글들을 재귀적으로 CommentDTO로 변환(빈 배열은 map이 실행되지 않음 -> 재귀 종료)
                List<CommentDTO> childComments = childEntities.stream()
                                .map(child -> toCommentDTO(child, childCommentMap, likeCountMap, likedCommentIds))
                                .collect(Collectors.toList());

                // Map에서 좋아요 수 조회 (미리 로드된 데이터 사용)
                Long likeCount = likeCountMap.getOrDefault(commentEntity.getCommentId(), 0L);

                // Set에서 좋아요 여부 확인 (미리 로드된 데이터 사용)
                Boolean isLikedByCurrentUser = likedCommentIds.contains(commentEntity.getCommentId());

                return new CommentDTO(
                                commentEntity.getCommentId(),
                                commentEntity.getAuthorEntity().getMemberId(),
                                commentEntity.getAuthorEntity().getName(),
                                commentEntity.getAuthorEntity().getProfileImgUrl(),
                                commentEntity.getContent(),
                                commentEntity.getCreatedAt(),
                                likeCount,
                                isLikedByCurrentUser,
                                childComments);
        }

        // CommentUpdateReqDTO -> CommentEntity
        @Mapping(target = "postEntity", ignore = true)
        @Mapping(target = "issueEntity", ignore = true)
        @Mapping(target = "authorEntity", ignore = true)
        void updateCommentEntity(@MappingTarget CommentEntity commentEntity, CommentUpdateReqDTO commentUpdateReqDTO);
}
