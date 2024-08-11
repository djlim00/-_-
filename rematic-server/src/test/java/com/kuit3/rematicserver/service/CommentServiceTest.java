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
    @DisplayName("검색어 정상 삭제 v2")
    void commentDeletionSuccessV2() {
        //1번 유저가 0L 게시물의 부모 댓글을 삭제했을 때 자식 댓글 까지 조회가 불가능한지 테스트
        //given
        //게시글 생성
        Long postId = postDao.createPost(new CreatePostRequest("댓글 등록 게시글", "게시글 내용",
                true, "카테고리", "장르", true,  1L, 1L));
        Long newPost = postDao.createPost(new CreatePostRequest("댓글 등록 게시글", "게시글 내용",
                true, "카테고리", "장르", true,  1L, 1L));
        //댓글 생성
        PostCommentRequest parentComment = new PostCommentRequest("this is test parentComment", 0L);
        PostCommentRequest childComment = new PostCommentRequest("this is test childComment", 1L);
        List<Long> result1 = new ArrayList<>();
        List<Long> result2 = new ArrayList<>();
        result1.add(0L);
        result2.add(0L);
        result1.add(1L);
        result2.add(1L);
        //when
        lenient().when(postInfoDao.leaveCommentWrittenByUser(1L, postId, parentComment))
                .thenReturn(result1);
        lenient().when(postInfoDao.leaveCommentWrittenByUser(1L, postId, childComment))
                .thenReturn(result2);
        lenient().when(commentDao.dormantValidatedComment(1L, 0L)).thenReturn(1);
        //then
        assertEquals(0L, postInfoDao.getCountOfComments(postId));
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
