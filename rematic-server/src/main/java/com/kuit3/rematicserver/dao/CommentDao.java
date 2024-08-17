package com.kuit3.rematicserver.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class CommentDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CommentDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public int modifyStatusDormantByPostId(Long postId) {
        String sql = "UPDATE Comment SET status='dormant' WHERE post_id = :post_id";
        MapSqlParameterSource param  = new MapSqlParameterSource()
                .addValue("post_id", postId);
        return jdbcTemplate.update(sql, param);
    }

    public List<Long> findByPostId(Long postId) {
        String sql = "select comment_id from Comment WHERE status='active' AND post_id = :post_id";
        MapSqlParameterSource param  = new MapSqlParameterSource()
                .addValue("post_id", postId);
        return jdbcTemplate.query(sql, param, (rs, r)->rs.getLong("comment_id"));
    }


    public void incrementLikes(Long commentId) {
        String sql = "UPDATE Comment SET likes = likes + 1 WHERE comment_id = :comment_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id",commentId);
        jdbcTemplate.update(sql, param);
    }


    public void decrementLikes(Long commentId) {
        String sql = "UPDATE Comment SET likes = likes - 1 WHERE comment_id = :comment_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId);
        jdbcTemplate.update(sql, param);
    }


    public void incrementHates(Long commentId) {
        String sql = "UPDATE Comment SET hates = hates + 1 WHERE comment_id = :comment_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId);
        jdbcTemplate.update(sql, param);
    }


    public void decrementHates(Long commentId) {
        String sql = "UPDATE Comment SET hates = hates - 1 WHERE comment_id = :comment_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId);
        jdbcTemplate.update(sql, param);
    }

    public boolean checkCommentExists(long userId, long commentId) {
        String sql = "select exists(select 1 from Comment where comment_id = :commentId and user_id = :userId and status = 'active');";
        Map<String, Object> param = Map.of("commentId", commentId, "userId", userId);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }

    public Integer dormantValidatedComment(long userId, long commentId) {
        String sql = "update Comment set status = 'dormant' where user_id = :userId and comment_id = :commentId;";
        Map<String ,Object> param = Map.of("userId", userId, "commentId", commentId);
        return jdbcTemplate.update(sql, param);
    }

    public boolean isCommentExists(long commentId) {
        String sql = "select exists(select 1 from Comment where comment_id = :commentId and status = 'active');";
        Map<String, Object> param = Map.of("commentId", commentId);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }

    public long getWriterId(long commentId) {
        String sql = "select user_id from Comment where comment_id = :commentId and status = 'active';";
        Map<String, Object> param = Map.of("commentId", commentId);
        return jdbcTemplate.queryForObject(sql, param, long.class);
    }

    public int reportViolatedComment(long commentId, long userId, long reportedUser, String type) {
        String sql = "insert into Comment_Report(reporter_id, reported_user_id, comment_id, created_at, type) " +
                "values(:userId, :reportedUser, :commentId, now(), :type);";
        Map<String, Object> param = Map.of(
                "userId", userId,
                "reportedUser", reportedUser,
                "commentId", commentId,
                "type", type
        );
        return jdbcTemplate.update(sql, param);
    }
}
