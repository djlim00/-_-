package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.PostReactionDao;
import com.kuit3.rematicserver.dao.PostDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostReactionService {
    private final PostReactionDao postReactionDao;
    private final PostDao postDao;

    @Transactional
    public void likePost(Long postId, Long userId) {
        if (postReactionDao.isLiked(postId, userId)) { // 이미 해당 유저가 해당 게시물에 좋아요를 눌렀다면
            postReactionDao.removeLike(postId, userId); // 해당 유저 좋아요 내역 삭제
            postDao.decrementLikes(postId); // 게시물 좋아요 수 감소
        } else { // 해당 유저가 해당 게시물에 좋아요를 누른 적이 없다면
            if (postReactionDao.isHated(postId, userId)) { // 싫어요를 누른 상태라면
                postReactionDao.removeHate(postId, userId); // 먼저 해당 유저의 해당 게시물 싫어요 내역 삭제
                postDao.decrementHates(postId); // 해당 게시물의 싫어요 개수 감수
            }
            postReactionDao.addLike(postId, userId); // 게시물에 좋아요 내역 추가
            postDao.incrementLikes(postId); // 게시물 좋아요 수 증가
        }
    }

    @Transactional
    public void hatePost(Long postId, Long userId) {
        if (postReactionDao.isHated(postId, userId)) {
            postReactionDao.removeHate(postId, userId);
            postDao.decrementHates(postId);
        } else {
            if (postReactionDao.isLiked(postId, userId)) {
                postReactionDao.removeLike(postId, userId);
                postDao.decrementLikes(postId);
            }
            postReactionDao.addHate(postId, userId);
            postDao.incrementHates(postId);
        }
    }
}
