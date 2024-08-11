package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.common.exception.UserCommentException;
import com.kuit3.rematicserver.dao.CommentDao;
import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.PostInfoDao;
import com.kuit3.rematicserver.dto.post.CreatePostRequest;
import com.kuit3.rematicserver.dto.post.PostCommentRequest;
import com.kuit3.rematicserver.dto.post.commentresponse.CommentInfo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentDao commentDao;
    @InjectMocks
    private PostService postService;
    @Mock
    private PostInfoDao postInfoDao;
    @Mock
    private PostDao postDao;

//    @BeforeEach
//    @DisplayName("댓글 테스트 환경 설정")
//    void init() {
//        Date currentTime = new Date(System.currentTimeMillis());
//        //부모댓글 2개, 첫번째 부모에 자식 댓글 2개인 상황
//        CommentInfo parent1 = new CommentInfo(1L, "parent1", 1L, "image1.jpg", "부모댓글1",
//                0L, new Timestamp(currentTime.getTime()), 0L, false, 0L, false, true);
//        CommentInfo firstChild1 = new CommentInfo(2L, "child1", 2L, "image1.jpg", "자식댓글1",
//                1L, new Timestamp(currentTime.getTime()-3600*1000), 0L, false, 0L, false, false);
//        CommentInfo secondChild1 = new CommentInfo(3L, "child2", 3L, "image1.jpg", "자식댓글2",
//                1L, new Timestamp(currentTime.getTime()-3600*1000), 0L, false, 0L, false, false);
//        CommentInfo parent2 = new CommentInfo(4L, "parent2", 4L, "image1.jpg", "부모댓글2",
//                0L, new Timestamp(currentTime.getTime()), 0L, false, 0L, false, true);
//    }
    @Test
    @Transactional
    @DisplayName("검색어 정상 삭제")
    void commentDeletionSuccess() {
        Long userId = 1L;
        //given
        given(commentDao.checkCommentExists(userId, 1L)).willReturn(true);
        given(commentDao.dormantValidatedComment(userId, 1L)).willReturn(1);
        //when
        String result = commentService.dormantUserComment(userId, 1L);
        //then
        assertEquals(result, "complete deleting comment");
    }

    @Test
    @Transactional
    @DisplayName("검색어 정상 삭제 V2")
    void commentDeletionSuccessV2() {
        //댓글을 적용할 게시글 생성
        Long userId = 1L;
        //게시글 생성Id = 0L
        Long postId = postDao.createPost(CreatePostRequest.builder()
                .title("댓글 등록 게시글")
                .content("게시글 내용")
                .category("카테고리")
                .genre("장르")
                .anonymity(true)
                .has_image(false)
                .bulletin_id(1L)
                .user_id(userId).build());
        List<Long> respect = new ArrayList<>();
        respect.add(0L);
        respect.add(1L);
        //given
        given(postInfoDao.isPostExists(postId)).willReturn(true);
        //부모댓글 생성
        PostCommentRequest parentComment = new PostCommentRequest("this is test parentComment", 0L);
        given(postInfoDao.leaveCommentWrittenByUser(userId, postId, parentComment)).willReturn(respect);
        //댓글 삭제 전제 조건
        given(commentDao.checkCommentExists(userId, 1L)).willReturn(true);
        //when
        commentDao.dormantValidatedComment(userId, 1L);
        //then
        assertEquals(postInfoDao.getCountOfComments(1L), 0);
//        //자식댓글 생성
        PostCommentRequest childComment = new PostCommentRequest("this is test childComment", 1L);
        postService.leaveNewComment(userId, postId, childComment);


    }

    @Test
    @Transactional
    @DisplayName("검색어 삭제 실패")
    void commentDeletionFail() {
        //given
        Long userId = 1L;
        given(commentDao.checkCommentExists(userId, 1L)).willReturn(false);
        //then
        assertThrows(UserCommentException.class, () -> {
            commentService.dormantUserComment(userId, 1L);
        });
    }
}
