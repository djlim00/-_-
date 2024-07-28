package com.kuit3.rematicserver.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Slf4j
@Repository
public class PostReactionDaoImpl implements PostReactionDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostReactionDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public boolean isLiked(Long postId, Long userId) {
        String sql = "SELECT COUNT(*) FROM PostLikes WHERE post_id = :post_id AND user_id = :user_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId)
                .addValue("user_id", userId);
        return jdbcTemplate.queryForObject(sql, param, Integer.class) > 0;
    }

    @Override
    public boolean isHated(Long postId, Long userId) {
        String sql = "SELECT COUNT(*) FROM PostHates WHERE post_id = :post_id AND user_id = :user_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId)
                .addValue("user_id", userId);
        return jdbcTemplate.queryForObject(sql, param, Integer.class) > 0;
    }

    @Override
    public void addLike(Long postId, Long userId) {
        String sql = "INSERT INTO PostLikes (post_id, user_id) VALUES (:post_id, :user_id)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId)
                .addValue("user_id", userId);
        jdbcTemplate.update(sql, param);
    }

    @Override
    public void removeLike(Long postId, Long userId) {
        String sql = "DELETE FROM PostLikes WHERE post_id = :post_id AND user_id = :user_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId)
                .addValue("user_id", userId);
        jdbcTemplate.update(sql, param);
    }

    @Override
    public void addHate(Long postId, Long userId) {
        String sql = "INSERT INTO PostHates (post_id, user_id) VALUES (:post_id, :user_id)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId)
                .addValue("user_id", userId);
        jdbcTemplate.update(sql, param);
    }

    @Override
    public void removeHate(Long postId, Long userId) {
        String sql = "DELETE FROM PostHates WHERE post_id = :post_id AND user_id = :user_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId)
                .addValue("user_id", userId);
        jdbcTemplate.update(sql, param);
    }
}
