package com.kuit3.rematicserver.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Slf4j
@Repository
public class CommentReactionDaoImpl implements CommentReactionDao{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CommentReactionDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public boolean isLiked(Long commentId, Long userId) {
        String sql = "SELECT COUNT(*) FROM CommentLikes WHERE comment_id = :comment_id AND user_id = :user_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId)
                .addValue("user_id", userId);
        return jdbcTemplate.queryForObject(sql, param, Integer.class) > 0;
    }

    @Override
    public boolean isHated(Long commentId, Long userId) {
        String sql = "SELECT COUNT(*) FROM CommentHates WHERE comment_id = :comment_id AND user_id = :user_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId)
                .addValue("user_id", userId);
        return jdbcTemplate.queryForObject(sql, param, Integer.class) > 0;
    }

    @Override
    public void addLike(Long commentId, Long userId) {
        String sql = "INSERT INTO CommentLikes (comment_id, user_id) VALUES (:comment_id, :user_id)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId)
                .addValue("user_id", userId);
        jdbcTemplate.update(sql, param);
    }

    @Override
    public void removeLike(Long commentId, Long userId) {
        String sql = "DELETE FROM CommentLikes WHERE comment_id = :comment_id AND user_id = :user_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId)
                .addValue("user_id", userId);
        jdbcTemplate.update(sql, param);
    }

    @Override
    public void addHate(Long commentId, Long userId) {
        String sql = "INSERT INTO CommentHates (comment_id, user_id) VALUES (:comment_id, :user_id)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId)
                .addValue("user_id", userId);
        jdbcTemplate.update(sql, param);
    }

    @Override
    public void removeHate(Long commentId, Long userId) {
        String sql = "DELETE FROM CommentHates WHERE comment_id = :comment_id AND user_id = :user_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId)
                .addValue("user_id", userId);
        jdbcTemplate.update(sql, param);
    }

    @Override
    public void blockUser(Long userId, Long blockId) {
        String sql ="INSERT INTO Blocked_User_List (user_id,block_id) VALUES (:user_id,:block_id)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("block_id", blockId);
        jdbcTemplate.update(sql, param);
    }
}
