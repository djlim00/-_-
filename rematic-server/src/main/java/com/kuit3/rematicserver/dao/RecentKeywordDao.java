package com.kuit3.rematicserver.dao;

import java.util.List;

public interface RecentKeywordDao {
    long saveKeyword(Long userId, String keyword);

    int deleteByUserId(long userId);

    List<Long> findByUserId(long createdUserId);
}
