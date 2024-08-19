package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.entity.Punishment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class PunishmentDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PunishmentDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private static RowMapper<Punishment> punishmentRowMapper(){
        return (rs, rowNum) -> {
            return Punishment.builder()
                .punishmentId(rs.getLong("punishment_id"))
                .reason(rs.getString("reason"))
                .content(rs.getString("content"))
                .endAt(rs.getTimestamp("end_at").toLocalDateTime())
                .userId(rs.getLong("user_id"))
                .build();
        };
    }

    public List<Punishment> findByUserId(long userId) {
        String sql = "SELECT * FROM Punishment WHERE user_id = :userId ORDER BY created_at DESC";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("userId", userId);
        return jdbcTemplate.query(sql, param, punishmentRowMapper());
    }


    public long create(Punishment punishment) {
        String sql = "INSERT INTO Punishment(reason, content, created_at, end_at, user_id) VALUES(:reason, :content, now(), :endAt, :userId)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("reason", punishment.getReason())
                .addValue("content", punishment.getContent())
                .addValue("endAt", Timestamp.valueOf(punishment.getEndAt()))
                .addValue("userId", punishment.getUserId());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public boolean hasUserReportedSameComment(long commentId, long userId) {
        String sql = "select exists (select 1 from Comment_Report where reporter_id = :userId " +
                "and comment_id = :commentId);";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("commentId", commentId);
        return jdbcTemplate.queryForObject(sql, param, boolean.class);
    }
}
