package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.entity.Bulletin;
import com.kuit3.rematicserver.entity.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<Comment> findByPostId(Long postId) {
        String sql = "select * from Comment WHERE status='active' AND post_id = :post_id";
        MapSqlParameterSource param  = new MapSqlParameterSource()
                .addValue("post_id", postId);
        return jdbcTemplate.query(sql, param, commentRowMapper());
    }

    public List<Comment> findById(Long commentId) {
        String sql = "select * from Comment WHERE status='active' AND comment_id = :comment_id";
        MapSqlParameterSource param  = new MapSqlParameterSource()
                .addValue("comment_id", commentId);
        return jdbcTemplate.query(sql, param, commentRowMapper());
    }

    private static RowMapper<Comment> commentRowMapper() {
        return (rs, r) -> {
            return Comment.builder()
                    .commentId(rs.getLong("comment_id"))
                    .sentences(rs.getString("sentences"))
                    .likes(rs.getLong("likes"))
                    .hates(rs.getLong("hates"))
                    .commentImageUrl(rs.getString("comment_image_url"))
                    .parentId(rs.getLong("parent_id"))
                    .status(rs.getString("status"))
                    .anonymity(rs.getString("anonymity"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .postId(rs.getLong("post_id"))
                    .userId(rs.getLong("user_id"))
                    .build();
        };
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

    public long reportViolatedComment(long commentId, long userId, long reportedUser, String type) {
        String sql = "insert into Comment_Report(reporter_id, reported_user_id, comment_id, created_at, type) " +
                "values(:userId, :reportedUser, :commentId, now(), :type);";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("reportedUser", reportedUser)
                .addValue("commentId", commentId)
                .addValue("type", type);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public int getLikeCount(Long commentId) {
        String sql = "SELECT likes FROM Comment WHERE comment_id = :comment_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId);
        return jdbcTemplate.queryForObject(sql, param, Integer.class);
    }

    public int getHateCount(Long commentId) {
        String sql = "SELECT hates FROM Comment WHERE comment_id = :comment_id";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("comment_id", commentId);
        return jdbcTemplate.queryForObject(sql, param, Integer.class);
    }

    public Bulletin findBulletinIdByCommentId(long commentId) {
        String sql = "select b.* from Bulletin b " +
                "join Post p on b.bulletin_id = p.bulletin_id " +
                "join Comment c on p.post_id = c.post_id " +
                "where c.comment_id = :commentId;";
        MapSqlParameterSource param = new MapSqlParameterSource().addValue("commentId", commentId);
        return jdbcTemplate.queryForObject(sql, param, bulletinRowMapper());
    }

    public RowMapper<Bulletin> bulletinRowMapper(){
        return (rs, rowNum) -> {
            Bulletin bulletin = Bulletin.builder()
                    .bulletinId(rs.getLong("bulletin_id"))
                    .name(rs.getString("name"))
                    .genre(rs.getString("genre"))
                    .category(rs.getString("category"))
                    .originCategory(rs.getString("origin_category"))
                    .thumbnailImageUrl(rs.getString("thumnail_image_url"))
//                    .PreviewVideoUrl(rs.getString("preview_video_url"))
                    .build();
            return bulletin;
        };
    }

    public int countReportByCommentId(long userId, long commentId) {
            String sql = "select count(*) from Comment_Report where comment_id = :commentId and reported_user_id = :userId;";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("commentId", commentId)
                .addValue("userId", userId);
        return jdbcTemplate.queryForObject(sql, param, Integer.class);
    }
}
