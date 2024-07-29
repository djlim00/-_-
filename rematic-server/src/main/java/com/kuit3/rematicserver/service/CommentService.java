package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.CommentDao;
import com.kuit3.rematicserver.dao.CommentHatesDao;
import com.kuit3.rematicserver.dao.CommentLikesDao;
import com.kuit3.rematicserver.dao.CommentReactionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        List<Long> postCommentIds = commentDao.findAllByPostId(postId);
        for(Long commentId : postCommentIds) {
            commentLikesDao.deleteByCommentId(commentId);
            commentHatesDao.deleteByCommentId(commentId);
        }
        commentDao.modifyStatusDormantByPostId(postId);
    }


    @Transactional
    public void likeComment(Long commentId, Long userId) {
        if (commentReactionDao.isLiked(commentId, userId)) { // 이미 해당 유저가 해당 게시물에 좋아요를 눌렀다면
            commentReactionDao.removeLike(commentId, userId); // 해당 유저 좋아요 내역 삭제
            commentDao.decrementLikes(commentId); // 게시물 좋아요 수 감소
        } else { // 해당 유저가 해당 게시물에 좋아요를 누른 적이 없다면
            if (commentReactionDao.isHated(commentId, userId)) { // 싫어요를 누른 상태라면
                commentReactionDao.removeHate(commentId, userId); // 먼저 해당 유저의 해당 게시물 싫어요 내역 삭제
                commentDao.decrementHates(commentId); // 해당 게시물의 싫어요 개수 감수
            }
            commentReactionDao.addLike(commentId, userId); // 게시물에 좋아요 내역 추가
            commentDao.incrementLikes(commentId); // 게시물 좋아요 수 증가
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
}
