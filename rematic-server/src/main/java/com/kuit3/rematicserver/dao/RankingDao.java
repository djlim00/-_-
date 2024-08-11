package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.home.GetRankedPostDto;
import com.kuit3.rematicserver.entity.Ranking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Slf4j
public class RankingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RankingDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public void clearRealTimeRanking() {
        String sql = "delete from Ranking where post_id > :stand;";
        Map<String, Object> param = Map.of("stand", 0);
        jdbcTemplate.update(sql, param);
    }

    public void updateRealTimeRanking() {
        String sql = "insert into Ranking (post_id, realtime_views) select post_id, realtime_views " +
                "from Post where (likes - hates) > 0 order by realtime_views DESC limit :cnt;";
        Map<String, Integer> param = Map.of("cnt", 10);
        jdbcTemplate.update(sql, param);
    }


    public void deleteAll() {
        String sql = "DELETE FROM Ranking WHERE 1=1";
        jdbcTemplate.update(sql, new MapSqlParameterSource());
    }

    public long save(Ranking ranking) {
        String sql = "insert into Ranking(recent_likes, recent_hates, category, post_id) values(:recentLikes, :recentHates, :category, :postId)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("recentLikes", ranking.getRecentLikes())
                .addValue("recentHates", ranking.getRecentHates())
                .addValue("category", ranking.getCategory())
                .addValue("postId", ranking.getPostId());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<Ranking> findByCategory(String category) {
        String sql = "SELECT * FROM Ranking " +
                "WHERE category = :category " +
                "ORDER BY ranking_id ASC";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("category", category);
        return jdbcTemplate.query(sql, param, rankingRowMapper());
    }

    public List<Ranking> findAllLimit(Long limit) {
        String sql = "SELECT * FROM Ranking ORDER BY recent_likes DESC, ranking_id ASC LIMIT :limit";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("limit", limit);
        return jdbcTemplate.query(sql, param, rankingRowMapper());
    }


    public boolean existsByPostId(Long postId) {
        String sql = "SELECT EXISTS (SELECT * FROM Ranking WHERE post_id = :postId)";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("postId", postId);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }

    private RowMapper<Ranking> rankingRowMapper(){
        return (rs, rowNum) -> {
            Ranking ranking = Ranking.builder()
                    .recentLikes(rs.getLong("recent_likes"))
                    .recentHates(rs.getLong("recent_hates"))
                    .category(rs.getString("category"))
                    .postId(rs.getLong("post_id"))
                    .build();
            return ranking;
        };
    }

}
