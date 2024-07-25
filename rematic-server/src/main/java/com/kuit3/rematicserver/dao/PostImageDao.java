package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.entity.PostImage;

import java.util.List;

public interface PostImageDao {

    Long savePostImage(Long postId, String fileName, String description);

    Long savePostImageWithoutDescription(Long postId, String fileUrl);

    int modifyStatusDormantByPostId(Long postId);

    List<PostImage> getByPostId(Long postId);
}
