package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.entity.PostImage;

import java.util.List;

public interface PostImageDao {

    Long save(Long postId, String fileName, String description);

    Long savePostImageWithoutDescription(Long postId, String fileUrl);

    Boolean hasImageUrlAlready(long commentId);

    int modifyStatusDormantByPostId(Long postId);

    List<PostImage> getByPostId(Long postId);

    int modifyStatusDormant(Long postImageId);

    void update(Long postImageId, String imageDescription, int order);

    PostImage getById(Long postImageId);

    void deleteById(Long postImageId);
}
