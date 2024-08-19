package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.DatabaseException;
import com.kuit3.rematicserver.common.exception.ReportCommentException;
import com.kuit3.rematicserver.common.exception.UserCommentException;
import com.kuit3.rematicserver.dao.*;
import com.kuit3.rematicserver.dto.comment.CommentReactionResponse;
import com.kuit3.rematicserver.entity.Bulletin;
import com.kuit3.rematicserver.entity.Comment;
import com.kuit3.rematicserver.entity.Punishment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentDao commentDao;
    private final CommentLikesDao commentLikesDao;
    private final CommentHatesDao commentHatesDao;
    private final CommentReactionDao commentReactionDao;
    private final PunishmentDao punishmentDao;

    private final int REPORT_THRESHOLD = 5;

    public void deleteCommentsOfPost(Long postId) {
        log.info("CommentService::deleteCommentsOfPost()");
        List<Comment> postComments = commentDao.findByPostId(postId);
        for(Comment comment : postComments) {
            long commentId = comment.getCommentId();
            commentLikesDao.deleteByCommentId(commentId);
            commentHatesDao.deleteByCommentId(commentId);
        }
        commentDao.modifyStatusDormantByPostId(postId);
    }


    @Transactional
    public CommentReactionResponse likeComment(Long commentId, Long userId) {
        boolean isLiked = commentReactionDao.isLiked(commentId, userId);
        boolean isHated = commentReactionDao.isHated(commentId, userId);

        if (isLiked) {
            commentReactionDao.removeLike(commentId, userId);
            commentDao.decrementLikes(commentId);
        } else {
            if (isHated) {
                commentReactionDao.removeHate(commentId, userId);
                commentDao.decrementHates(commentId);
            }
            commentReactionDao.addLike(commentId, userId);
            commentDao.incrementLikes(commentId);
        }

        int likeCount = commentDao.getLikeCount(commentId);

        return new CommentReactionResponse(likeCount, null, !isLiked, null);
    }

    @Transactional
    public CommentReactionResponse hateComment(Long commentId, Long userId) {
        boolean isHated = commentReactionDao.isHated(commentId, userId);
        boolean isLiked = commentReactionDao.isLiked(commentId, userId);

        if (isHated) {
            commentReactionDao.removeHate(commentId, userId);
            commentDao.decrementHates(commentId);
        } else {
            if (isLiked) {
                commentReactionDao.removeLike(commentId, userId);
                commentDao.decrementLikes(commentId);
            }
            commentReactionDao.addHate(commentId, userId);
            commentDao.incrementHates(commentId);
        }

        int hateCount = commentDao.getHateCount(commentId);

        return new CommentReactionResponse(null, hateCount, null, !isHated);
    }

    public void blockUser(Long userId, Long blockId) {
        commentReactionDao.blockUser(userId,blockId);
    }

    public String dormantUserComment(long userId, long commentId) {
        log.info("PostService.dormantUserComment");
        if (!commentDao.checkCommentExists(userId, commentId)) {
            throw new UserCommentException(COMMENT_NOT_FOUND);
        }
        int result = commentDao.dormantValidatedComment(userId, commentId);
        if (result == 1) {
            return "complete deleting comment";
        } else {
            throw new DatabaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public String reportUserComment(long userId, long commentId, String type) {
        log.info("CommentService.reportUserComment");
        if(!commentDao.isCommentExists(commentId)) {
            throw new UserCommentException(COMMENT_NOT_FOUND);
        }
        if(punishmentDao.hasUserReportedSameComment(commentId, userId)) {
            throw new ReportCommentException(COMMENT_REPORT_EXISTS);
        }
        long reportedUserId = commentDao.getWriterId(commentId);
        long result = commentDao.reportViolatedComment(commentId, userId, reportedUserId, type);
        int cnt = commentDao.countReportByCommentId(reportedUserId, commentId);
        log.info("count of report = " + cnt);

        if(cnt == REPORT_THRESHOLD) {
            log.info("punishTargetUser method called");
            log.info("Add Punishment + userId = " + userId + " commentId = " + commentId);
            punishTargetUser(commentId, type, reportedUserId);
        }
        return "complete reporting violated Comment";
    }

    private void punishTargetUser(long commentId, String type, long reportedUserId) {
        List<Punishment> list = punishmentDao.findByUserId(reportedUserId);
        long punishmentCount = list.size();
        LocalDateTime endAt;
        log.info("list.size() = " + list.size());

        if(!list.isEmpty()){
            endAt = list.get(list.size() - 1).getEndAt();
        }
        else{
            endAt = LocalDateTime.now();
        }

        String content = "경고";
        if(punishmentCount + 1 == 6){
            endAt = endAt.plusDays(14);
            content = "이용정지 14일";
        }
        else if(punishmentCount + 1 == 4){
            endAt = endAt.plusDays(7);
            content = "이용정지 7일";
        }
        else if(punishmentCount + 1 == 3){
            endAt = endAt.plusDays(3);
            content = "이용정지 3일";
        }
        else if(punishmentCount + 1 == 2){
            endAt = endAt.plusDays(1);
            content = "이용정지 1일";
        }

        String reason;
        Bulletin bulletin = commentDao.findBulletinIdByCommentId(commentId);
        reason = bulletin.getName() + " 게시판 (";
        if(type.equals("abuse")){
            reason += "욕설/비하";
        }
        else if(type.equals("obscene")){
            reason += "음란물/불건전 내용";
        }
        else if(type.equals("unrelated")){
            reason += "게시판 성격 부적절";
        }
        else if(type.equals("advertisement")){
            reason += "상업성 광고 및 판매";
        }
        reason += ")";

        Punishment punishment = Punishment
                .builder()
                .reason(reason)
                .content(content)
                .endAt(endAt)
                .userId(reportedUserId)
                .bulletinId(bulletin.getBulletinId())
                .build();

        punishmentDao.create(punishment);
    }
}
