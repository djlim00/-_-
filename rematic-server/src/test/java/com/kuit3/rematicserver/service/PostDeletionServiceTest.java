package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.PostDao;
import com.kuit3.rematicserver.dto.post.CreatePostRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class PostDeletionServiceTest {
    @Autowired
    private PostDeletionService postDeletionService;

    @Autowired
    private PostDao postDao;



    @Test
    @Transactional
    public void 게시물_삭제(){
        //given
        long createdPostId = postDao.createPost(CreatePostRequest.builder()
                .title("삭제될 게시물")
                .content("삭제될 내용")
                .category("webtoon")
                .anonymity(true)
                .has_image(true)
                .bulletin_id(1L)
                .user_id(1L).build());



        //when

        //then
    }


}