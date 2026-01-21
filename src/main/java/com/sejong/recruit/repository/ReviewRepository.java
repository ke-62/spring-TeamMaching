package com.sejong.recruit.repository;

import com.sejong.recruit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 사용자가 받은 모든 평가 조회
     */
    List<Review> findByReviewedUserOrderByCreatedAtDesc(User reviewedUser);

    /**
     * 특정 사용자가 작성한 모든 평가 조회
     */
    List<Review> findByReviewerOrderByCreatedAtDesc(User reviewer);

    /**
     * 특정 프로젝트에 대한 모든 평가 조회
     */
    @Query("SELECT r FROM Review r WHERE r.recruitPost.id = :recruitPostId")
    List<Review> findByRecruitPostId(@Param("recruitPostId") Long recruitPostId);

    /**
     * 특정 사용자의 평균 점수 계산
     */
    @Query("SELECT AVG(r.collaborationScore) FROM Review r WHERE r.reviewedUser.id = :userId")
    Double getAverageCollaborationScore(@Param("userId") Long userId);

    @Query("SELECT AVG(r.technicalScore) FROM Review r WHERE r.reviewedUser.id = :userId")
    Double getAverageTechnicalScore(@Param("userId") Long userId);

    @Query("SELECT AVG(r.responsibilityScore) FROM Review r WHERE r.reviewedUser.id = :userId")
    Double getAverageResponsibilityScore(@Param("userId") Long userId);

    /**
     * 특정 사용자가 받은 평가 개수
     */
    Long countByReviewedUser(User reviewedUser);

    /**
     * 중복 평가 방지: 같은 사람이 같은 프로젝트에서 같은 대상에게 이미 평가했는지 확인
     */
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.reviewer.id = :reviewerId AND r.reviewedUser.id = :reviewedUserId AND r.recruitPost.id = :recruitPostId")
    boolean existsByReviewerAndReviewedUserAndRecruitPost(
            @Param("reviewerId") Long reviewerId,
            @Param("reviewedUserId") Long reviewedUserId,
            @Param("recruitPostId") Long recruitPostId
    );
}
