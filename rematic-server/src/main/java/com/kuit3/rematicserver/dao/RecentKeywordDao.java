package com.kuit3.rematicserver.dao;

public interface RecentKeywordDao {
    long saveKeyword(Long userId, String keyword);
}
