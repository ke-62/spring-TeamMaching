package com.sejong.recruit.repository;

import com.sejong.recruit.domain.review.entity.PeerReview;
import com.sejong.recruit.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeerReviewRepository extends JpaRepository<PeerReview, Long> {

    List<PeerReview> findByEvaluateeOrderByCreatedAtDesc(User evaluatee);

    List<PeerReview> findByEvaluatorOrderByCreatedAtDesc(User evaluator);

    @Query("SELECT pr FROM PeerReview pr WHERE pr.project.id = :projectId")
    List<PeerReview> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT AVG(pr.ratingCollaboration) FROM PeerReview pr WHERE pr.evaluatee.id = :userId")
    Double getAverageCollaborationScore(@Param("userId") Long userId);

    @Query("SELECT AVG(pr.ratingTechnical) FROM PeerReview pr WHERE pr.evaluatee.id = :userId")
    Double getAverageTechnicalScore(@Param("userId") Long userId);

    @Query("SELECT AVG(pr.ratingCommunication) FROM PeerReview pr WHERE pr.evaluatee.id = :userId")
    Double getAverageCommunicationScore(@Param("userId") Long userId);

    List<PeerReview> findByEvaluateeId(Long evaluateeId);

    Long countByEvaluatee(User evaluatee);

    boolean existsByEvaluatorAndEvaluateeAndProjectId(User evaluator, User evaluatee, Long projectId);
}
