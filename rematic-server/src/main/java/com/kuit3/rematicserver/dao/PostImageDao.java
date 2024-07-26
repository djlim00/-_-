package com.kuit3.rematicserver.dao;

public interface PostImageDao {

    Long savePostImage(Long postId, String fileName, String description);

    Long savePostImageWithoutDescription(Long postId, String fileUrl);

    Boolean hasImageUrlAlready(long commentId);

    int modifyStatusDormantByPostId(Long postId);
}
