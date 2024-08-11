package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.CommentDao;
import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.PostInfoDao;
import com.kuit3.rematicserver.dto.post.CreatePostRequest;
import com.kuit3.rematicserver.dto.post.PostCommentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class CommentDeletionTest {

    @Autowired
    private CommentDao commentDao;
    @Autowired
    private PostInfoDao postInfoDao;
    @Autowired
    private PostDao postDao;

        @DisplayName("댓글 정상 삭제")
        @Test
        @Transactional
        void commentDeletionSuccess() {
            //1번 유저가 0L 게시물의 부모 댓글을 삭제했을 때 자식 댓글 까지 조회가 불가능한지 테스트
            //given
            Long postId = postDao.createPost(new CreatePostRequest("댓글 등록 게시글", "게시글 내용",
                    true, "카테고리", "장르", true,  1L, 1L));
            assertEquals(0L, postId);
            PostCommentRequest parentComment = new PostCommentRequest("this is test parentComment", 0L);
            PostCommentRequest childComment = new PostCommentRequest("this is test childComment", 1L);
            postInfoDao.leaveCommentWrittenByUser(1L, postId, parentComment);
            postInfoDao.leaveCommentWrittenByUser(1L, postId, childComment);
            //when
            commentDao.dormantValidatedComment(1L, 0L);
            //then
            assertEquals(postInfoDao.getCountOfComments(postId), 0);
    }
}
