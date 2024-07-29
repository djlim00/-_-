package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.post.CreatePostRequest;
import com.kuit3.rematicserver.dto.post.SearchPostDto;
import com.kuit3.rematicserver.entity.Post;

import java.util.List;

public interface PostDao {
    public List<SearchPostDto> getPage(String keyword, String category, Long lastId, Long limit);
    public boolean hasNextPage(String keyword, String category, Long lastId);
    public long createPost(CreatePostRequest request);

    boolean hasPostWithId(Long postId);
    void incrementLikes(Long postId);
    void decrementLikes(Long postId);
    void incrementHates(Long postId);
    void decrementHates(Long postId);

    public Post findById(Long postId);
    public boolean existsById(Long postId);
    public int modifyStatusDormant(Long postId);

    public int update(Long postId, String title, String content);

    public List<SearchPostDto> getBulletinPosts(Long bulletinId, String keyword, Long lastId, Long limit);

    public boolean hasNextBulletinPage(Long bulletinId, String keyword, Long lastPostId);

}
