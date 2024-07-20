package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.post.GetPostDto;

import java.util.List;

public interface PostDao {
    public List<GetPostDto> getPage(String keyword, String category, Long lastId);
    public boolean hasNextPage(String keyword, String category, Long lastId);
}
