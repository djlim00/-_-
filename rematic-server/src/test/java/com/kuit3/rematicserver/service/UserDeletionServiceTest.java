package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.*;
import com.kuit3.rematicserver.dto.auth.CreateUserDTO;
import com.kuit3.rematicserver.dto.post.CreatePostRequest;
import com.kuit3.rematicserver.dto.post.PostCommentRequest;
import com.kuit3.rematicserver.dto.post.commentresponse.CommentInfo;
import com.kuit3.rematicserver.entity.Post;
import com.kuit3.rematicserver.entity.UserScrap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
class UserDeletionServiceTest {
    @Autowired
    private UserDeletionService userDeletionService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserScrapDao userScrapDao;

    @Autowired
    private RecentKeywordDao recentKeywordDao;
    
    @Autowired
    private PostInfoDao postInfoDao;

    @Test
    public void 회원탈퇴_테스트(){
        // given
        String createdUserEmail = "test3@test.com";
        long createdUserId = userDao.createUser(CreateUserDTO.builder()
                .nickname("이카니2")
                .email(createdUserEmail)
                .profile_image_url("wihdfoiwdfod")
                .build());

        postDao.createPost(CreatePostRequest.builder()
                .title("아")
                .content("아")
                .has_image(true)
                .anonymity(true)
                .category("웹툰")
                .genre("코미디")
                .user_id(createdUserId)
                .bulletin_id(1L)
                .build());

        long postId = postDao.createPost(CreatePostRequest.builder()
                .title("아")
                .content("아")
                .has_image(true)
                .anonymity(true)
                .category("웹툰")
                .genre("코미디")
                .user_id(1L)
                .bulletin_id(1L)
                .build());

        userScrapDao.save(createdUserId, postId);
        recentKeywordDao.saveKeyword(createdUserId, "기록");

        String expectedComment = "댓글이야.";
        String expectedComment2 = "대댓글이야";
        String expectedWriter = "알수없음";
        List<Long> commentId = postInfoDao.leaveCommentWrittenByUser(createdUserId, postId, new PostCommentRequest(expectedComment, 0L));
        postInfoDao.leaveCommentWrittenByUser(createdUserId, postId, new PostCommentRequest(expectedComment2, commentId.get(0)));

        //when
        userDeletionService.handle(createdUserId);

        // then
        assertThat(userDao.hasUserWithDuplicateEmail(createdUserEmail)).isEqualTo(false);
        assertThat(postDao.findByUserId(createdUserId)).isEqualTo(new ArrayList<Post>());
        assertThat(userScrapDao.findByUserId(createdUserId)).isEqualTo(new ArrayList<UserScrap>());
        assertThat(recentKeywordDao.findByUserId(createdUserId)).isEqualTo(new ArrayList<Long>());

        List<CommentInfo> parentComments = postInfoDao.getLikeStandCommentsByPostId(postId);
        log.info("parentCommentIds=" + parentComments);

        assertThat(parentComments.get(0).getWriter()).isEqualTo(expectedWriter); // 부모 댓글 검증
        assertThat(parentComments.get(0).getComment()).isEqualTo(expectedComment);

        List<CommentInfo> childComments = postInfoDao.getChildCommentsWithoutPrefer(parentComments.stream()
                .map(CommentInfo::getCommentId)
                .collect(Collectors.toList()));
        log.info("childComments=" + childComments);

        assertThat(childComments.get(0).getWriter()).isEqualTo(expectedWriter); // 자식 댓글 검증
        assertThat(childComments.get(0).getComment()).isEqualTo(expectedComment2);
    }
}