package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.CommentNotFoundException;
import com.kuit3.rematicserver.common.exception.DatabaseException;
import com.kuit3.rematicserver.common.exception.UserCommentException;
import com.kuit3.rematicserver.dao.CommentDao;
import com.kuit3.rematicserver.dao.CommentHatesDao;
import com.kuit3.rematicserver.dao.CommentLikesDao;
import com.kuit3.rematicserver.dao.CommentReactionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.COMMENT_NOT_FOUND;
import static com.kuit3.rematicserver.common.response.status.BaseExceptionResponseStatus.DATABASE_ERROR;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentDao commentDao;
    private final CommentLikesDao commentLikesDao;
    private final CommentHatesDao commentHatesDao;
    private final CommentReactionDao commentReactionDao;

    public void deleteCommentsOfPost(Long postId) {
        log.info("CommentService::deleteCommentsOfPost()");
        List<Long> postCommentIds = commentDao.findByPostId(postId);
        for(Long commentId : postCommentIds) {
            commentLikesDao.deleteByCommentId(commentId);
            commentHatesDao.deleteByCommentId(commentId);
        }
        commentDao.modifyStatusDormantByPostId(postId);
    }


    @Transactional
    public void likeComment(Long commentId, Long userId) {
        if (commentReactionDao.isLiked(commentId, userId)) { // 이미 해당 유저가 해당 댓글에 좋아요를 눌렀다면
            commentReactionDao.removeLike(commentId, userId); // 해당 유저 좋아요 내역 삭제
            commentDao.decrementLikes(commentId); // 댓글 좋아요 수 감소
        } else { // 해당 유저가 해당 댓글에 좋아요를 누른 적이 없다면
            if (commentReactionDao.isHated(commentId, userId)) { // 싫어요를 누른 상태라면
                commentReactionDao.removeHate(commentId, userId); // 먼저 해당 유저의 해당 댓글 싫어요 내역 삭제
                commentDao.decrementHates(commentId); // 해당 댓글의 싫어요 개수 감수
            }
            commentReactionDao.addLike(commentId, userId); // 댓글에 좋아요 내역 추가
            commentDao.incrementLikes(commentId); // 댓글 좋아요 수 증가
        }
    }

    @Transactional
    public void hateComment(Long commentId, Long userId) {
        if (commentReactionDao.isHated(commentId, userId)) {
            commentReactionDao.removeHate(commentId, userId);
            commentDao.decrementHates(commentId);
        } else {
            if (commentReactionDao.isLiked(commentId, userId)) {
                commentReactionDao.removeLike(commentId, userId);
                commentDao.decrementLikes(commentId);
            }
            commentReactionDao.addHate(commentId, userId);
            commentDao.incrementHates(commentId);
        }
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

    public String reportUserComment(long userId, long commentId, String type) {
        log.info("CommentService.reportUserComment");
        if(!commentDao.isCommentExists(commentId)) {
            throw new CommentNotFoundException(COMMENT_NOT_FOUND);
        }
        long reportedUser = commentDao.getWriterId(commentId);
        int result = commentDao.reportViolatedComment(commentId, userId, reportedUser, type);
        if(result != 1) {
            throw new DatabaseException(DATABASE_ERROR);
        } else {
            //신고 횟수 확인 후 punishment 테이블에 데이터 삽입 여부 메서드 넣을 자리
            return "complete reporting violated Comment";
        }
    }
}
