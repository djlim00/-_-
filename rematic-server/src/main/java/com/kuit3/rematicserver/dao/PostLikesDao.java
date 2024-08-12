package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.entity.PostLikes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Slf4j
@Repository
public class PostLikesDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostLikesDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public int deleteByPostId(Long postId) {
        String sql = "DELETE FROM PostLikes WHERE post_id = :post_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("post_id", postId);
        return jdbcTemplate.update(sql, param);
    }
}
