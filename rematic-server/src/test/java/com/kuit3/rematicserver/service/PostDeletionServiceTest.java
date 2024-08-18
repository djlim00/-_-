package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.*;
import com.kuit3.rematicserver.dto.post.CreatePostRequest;
import com.kuit3.rematicserver.dto.post.PostCommentRequest;
import com.kuit3.rematicserver.entity.UserScrap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Transactional
@SpringBootTest
class PostDeletionServiceTest {
    @Autowired
    private PostDeletionService postDeletionService;
    @Autowired
    private PostDao postDao;
    @Autowired
    private PostLikesDao postLikesDao;
    @Autowired
    private PostHatesDao postHatesDao;
    @Autowired
    private UserScrapDao userScrapDao;
    @Autowired
    private PostInfoDao postInfoDao;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private PostReactionDao postReactionDao
            ;
    @Test
    public void 게시물_삭제(){
        //given
        long createdPostId = postDao.createPost(CreatePostRequest.builder()
                .title("삭제될 게시물")
                .content("삭제될 내용")
                .category("webtoon")
                .genre("로맨스")
                .anonymity(true)
                .has_image(true)
                .bulletin_id(1L)
                .user_id(2L).build());
        postReactionDao.addLike(createdPostId, 1L);
        postReactionDao.addHate(createdPostId, 1L);
        userScrapDao.save(1L, createdPostId);
        postInfoDao.leaveCommentWrittenByUser(2L, createdPostId, new PostCommentRequest("댓글", 0L, false));

        //when
        postDeletionService.handle(createdPostId);

        //then
        assertThatThrownBy(()->postDao.findById(createdPostId)).isInstanceOf(EmptyResultDataAccessException.class);
        assertThat(postLikesDao.findByPostId(createdPostId)).isEqualTo(new ArrayList<Long>());
        assertThat(postHatesDao.findByPostId(createdPostId)).isEqualTo(new ArrayList<Long>());
        assertThat(userScrapDao.findByPostId(createdPostId)).isEqualTo(new ArrayList<UserScrap>());
        assertThat(commentDao.findByPostId(createdPostId)).isEqualTo(new ArrayList<Long>());
    }
}