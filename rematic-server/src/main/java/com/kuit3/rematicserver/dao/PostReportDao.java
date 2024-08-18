package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.post.PostReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Objects;

@Slf4j
@Repository
public class PostReportDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostReportDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public long create(PostReport dto) {
        String sql = "insert into Post_Report(type, reporter_id, reported_user_id, post_id) values(:type, :reporterId, :reportedUserId, :postId)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("type", dto.getType())
                .addValue("reporterId", dto.getReporterId())
                .addValue("reportedUserId", dto.getReportedUserId())
                .addValue("postId", dto.getPostId());

        KeyHolder keyholder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyholder);
        return Objects.requireNonNull(keyholder.getKey()).longValue();
    }

    public long countByPostId(long postId) {
        String sql = "select count(*) from Post_Report where post_id = :postId";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("postId", postId);
        return jdbcTemplate.queryForObject(sql, param, long.class);
    }

    public long countByPostIdAndType(long postId, String type) {
        String sql = "select count(*) from Post_Report where post_id = :postId and type = :type";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("type", type);

        return jdbcTemplate.queryForObject(sql, param, long.class);
    }
}
