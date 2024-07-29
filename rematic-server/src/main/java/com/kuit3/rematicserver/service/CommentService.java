package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.CommentDao;
import com.kuit3.rematicserver.dao.CommentHatesDao;
import com.kuit3.rematicserver.dao.CommentLikesDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentDao commentDao;
    private final CommentLikesDao commentLikesDao;
    private final CommentHatesDao commentHatesDao;

    public void deleteCommentsOfPost(Long postId) {
        log.info("CommentService::deleteCommentsOfPost()");
        List<Long> postCommentIds = commentDao.findAllByPostId(postId);
        for(Long commentId : postCommentIds) {
            commentLikesDao.deleteByCommentId(commentId);
            commentHatesDao.deleteByCommentId(commentId);
        }
        commentDao.modifyStatusDormantByPostId(postId);
    }
}
