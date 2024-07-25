package com.kuit3.rematicserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostDeletionService {
    private final PostService postService;
    private final PostLikesService postLikesService;
    private final PostHatesService postHatesService;
    private final UserScrapService userScrapService;
    private final CommentService commentService;
    private final RankingService rankingService;

    @Transactional
    public void handle(Long postId){
        log.info("PostDeletionService::deletePost()");
        postLikesService.deleteLikesOfPost(postId);
        postHatesService.deleteHatesOfPost(postId);
        userScrapService.deleteScrapsOfPost(postId);
        commentService.deleteCommentsOfPost(postId);

        // 랭킹 기능 수정 시 구현할 코드
        //rankingService.deletePostFromRanking(postId);
        postService.deletePost(postId);
    }
}
