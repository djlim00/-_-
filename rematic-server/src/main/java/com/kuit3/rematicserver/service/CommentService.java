package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.DatabaseException;
import com.kuit3.rematicserver.common.exception.UserCommentException;
import com.kuit3.rematicserver.dao.CommentDao;
import com.kuit3.rematicserver.dao.CommentHatesDao;
import com.kuit3.rematicserver.dao.CommentLikesDao;
import com.kuit3.rematicserver.dao.CommentReactionDao;
import com.kuit3.rematicserver.dto.comment.CommentReactionResponse;
import com.kuit3.rematicserver.entity.Comment;
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
}
