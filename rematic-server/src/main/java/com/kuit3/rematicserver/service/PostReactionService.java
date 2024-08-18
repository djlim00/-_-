package com.kuit3.rematicserver.service;

import com.kuit3.rematicserver.dao.PostReactionDao;
import com.kuit3.rematicserver.dao.PostDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostReactionService {
    private final PostReactionDao postReactionDao;
    private final PostDao postDao;

    @Transactional
    public Map<String, Object> likePost(Long postId, Long userId) {
        boolean isLiked;
        if (postReactionDao.isLiked(postId, userId)) {
            postReactionDao.removeLike(postId, userId);
            postDao.decrementLikes(postId);
            isLiked = false;
        } else {
            if (postReactionDao.isHated(postId, userId)) {
                postReactionDao.removeHate(postId, userId);
                postDao.decrementHates(postId);
            }
            postReactionDao.addLike(postId, userId);
            postDao.incrementLikes(postId);
            isLiked = true;
        }

        int likeCount = postDao.getLikeCount(postId);
        Map<String, Object> response = new HashMap<>();
        response.put("isLiked", isLiked);
        response.put("likeCount", likeCount);
        return response;
    }

    @Transactional
    public Map<String, Object> hatePost(Long postId, Long userId) {
        boolean isHated;
        if (postReactionDao.isHated(postId, userId)) {
            postReactionDao.removeHate(postId, userId);
            postDao.decrementHates(postId);
            isHated = false;
        } else {
            if (postReactionDao.isLiked(postId, userId)) {
                postReactionDao.removeLike(postId, userId);
                postDao.decrementLikes(postId);
            }
            postReactionDao.addHate(postId, userId);
            postDao.incrementHates(postId);
            isHated = true;
        }

        int hateCount = postDao.getHateCount(postId);
        Map<String, Object> response = new HashMap<>();
        response.put("isHated", isHated);
        response.put("hateCount", hateCount);
        return response;
    }
}
