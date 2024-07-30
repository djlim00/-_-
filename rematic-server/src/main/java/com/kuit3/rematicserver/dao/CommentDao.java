package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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

    public List<Long> findAllByPostId(Long postId) {
        String sql = "select comment_id from Comment WHERE post_id = :post_id";
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
}
