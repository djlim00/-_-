package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dao.UserDao;
import com.kuit3.rematicserver.dao.UserScrapDao;
import com.kuit3.rematicserver.dto.auth.CreateUserDTO;
import com.kuit3.rematicserver.dto.post.CreatePostRequest;
import com.kuit3.rematicserver.dto.post.CreatePostResponse;
import com.kuit3.rematicserver.dto.post.GetClickedPostResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserScrapDao userScrapDao;

    @Autowired
    private UserScrapService userScrapService;

    @Test
    public void 게시글_생성(){
        //given
        long userId = userDao.createUser(CreateUserDTO.builder()
                .nickname("이름")
                .profile_image_url("image")
                .email("atest@test.com").build());

        Boolean expectedValue1 = false;
        Boolean expectedValue2 = true;
        CreatePostRequest request = CreatePostRequest.builder()
                .title("생성된 글 제목")
                .content("생성된 글 내용")
                .has_image(expectedValue1)
                .category("웹툰")
                .genre("로맨스")
                .anonymity(expectedValue2)
                .user_id(userId)
                .bulletin_id(1L)
                .build();
        //when
        CreatePostResponse response = postService.createPost(request);

        //then
        assertThat(postDao.findById(response.getPost_id())).isNotNull();
        assertThat(postDao.findById(response.getPost_id()).getHasImage()).isEqualTo(expectedValue1);
        assertThat(postDao.findById(response.getPost_id()).getAnonymity()).isEqualTo(expectedValue2);
    }

    @Test
    public void 스크랩_후_회원_게시글_조회(){
        //given
        long userId = userDao.createUser(CreateUserDTO.builder()
                .nickname("이름")
                .profile_image_url("image")
                .email("atest@test.com").build());


        CreatePostResponse res = postService.createPost(CreatePostRequest.builder()
                .title("생성된 글 제목")
                .content("생성된 글 내용")
                .has_image(false)
                .anonymity(false)
                .user_id(userId)
                .bulletin_id(1L)
                .build());
        long postId = res.getPost_id();

        long scrapId = userScrapService.create(userId, postId);

        //when
        GetClickedPostResponse response = postService.getValidatedClickedPostInfo(userId, postId);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getPostInfo().getIsScraped()).isEqualTo(true);
        assertThat(response.getPostInfo().getScrapId()).isEqualTo(scrapId);
        assertThat(response.getPostInfo().getScraps()).isEqualTo(1L);
    }
}