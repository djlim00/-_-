package com.kuit3.rematicserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDeletionService {
    private final UserService userService;
    private final UserScrapService userScrapService;
    private final PostService postService;
    private final PostDeletionService postDeletionService;
    private final RecentKeywordService recentKeywordService;

    @Transactional
    public void handle(long userId){
        userScrapService.deleteByUserId(userId);
        List<Long> postIds = postService.findByUserId(userId);
        for(Long postId : postIds){
            postDeletionService.handle(postId);
        }
//        commentService.deleteByUserId();
        recentKeywordService.deleteByUserId(userId);
        userService.deleteUser(userId);
    }
}
