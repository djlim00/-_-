package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.CreatePostRequest;
import com.kuit3.rematicserver.dto.post.GetSearchPostDto;
import com.kuit3.rematicserver.entity.Post;

import java.util.List;

public interface PostDao {
    public List<GetSearchPostDto> getPage(String keyword, String category, Long lastId, Long limit);
    public boolean hasNextPage(String keyword, String category, Long lastId);
    public long createPost(CreatePostRequest request);
    Post findById(Long postId);
    boolean hasPostWithId(Long postId);
    int modifyStatusDormant(Long postId);

}
