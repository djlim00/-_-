package com.kuit3.rematicserver.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Slf4j
@Repository
public class CommentHatesDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CommentHatesDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    public int deleteByCommentId(Long commentId) {
        String sql = "DELETE FROM CommentHates WHERE comment_id = :comment_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId);
        return jdbcTemplate.update(sql, param);
    }
}
