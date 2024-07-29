package com.kuit3.rematicserver.dao;

public interface PostReactionDao {
    boolean isLiked(Long postId, Long userId);
    boolean isHated(Long postId, Long userId);
    void addLike(Long postId, Long userId);
    void removeLike(Long postId, Long userId);
    void addHate(Long postId, Long userId);
    void removeHate(Long postId, Long userId);
}