package com.kuit3.rematicserver.dao;

import com.kuit3.rematicserver.dto.post.commentresponse.CommentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.kuit3.rematicserver.dto.post.postresponse.ImageInfo;
import com.kuit3.rematicserver.dto.post.postresponse.PostInfo;
import com.kuit3.rematicserver.dto.post.postresponse.UserInfo;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class PostInfoDao {
    NamedParameterJdbcTemplate jdbcTemplate;

    public PostInfoDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public boolean isPostExists(long postId) {
        String sql = "select exists (select 1 from Post where post_id = :postId);";
        Map<String, Object> param = Map.of("postId", postId);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }

    public boolean isPostAnonymous(long postId) {
        String sql = "select anonymity = '공개' from Post where post_id = :postId;";
        Map<String, Object> param = Map.of("postId", postId);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }

    public UserInfo getWriterInfo(long postId) {
        String sql = "select u.nickname, u.profile_image_url from User u " +
                "join Post p on u.user_id = p.user_id " +
                "where p.post_id = :postId;";
        Map<String, Object> param = Map.of("postId", postId);
        return jdbcTemplate.queryForObject(sql, param, (rs, rowNum) -> {
            return new UserInfo(
                    rs.getString("nickname"),
                    rs.getString("profile_image_url")
            );
        });
    }

    public String getBulletinInfo(long postId) {
        String sql = "select b.name from Bulletin b join Post p on p.bulletin_id = b.bulletin_id " +
                "where p.post_id = :postId;";
        return jdbcTemplate.queryForObject(sql, Map.of("postId", postId), String.class);
    }

    public PostInfo getPostInfo(long postId) {
        String sql = "select title, content, likes, hates, scraps from Post where post_id = :postId;";
        return jdbcTemplate.queryForObject(sql, Map.of("postId", postId), (rs, rowNum) -> {
            return new PostInfo(
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getLong("likes"),
                    false,
                    rs.getLong("hates"),
                    false,
                    rs.getLong("scraps"),
                    false
            );
        });
    }

    public List<ImageInfo> getImageInfo(long postId) {
        String sql = "select image_url, description from PostImage where post_id = :postId and status = 'active';";
        return jdbcTemplate.query(sql, Map.of("postId", postId), (rs, rowNum) -> {
            return new ImageInfo(
                    rs.getString("image_url"),
                    rs.getString("description")
            );
        });
    }

    public List<Boolean> checkUserPrefer(long userId, long postId) {
        String sql = "select " +
                "exists (select 1 from PostLikes where user_id = :userId and post_id = :postId) as isPostLikes, " +
                "exists (select 1 from PostHates where user_id = :userId and post_id = :postId) as isPostHates, " +
                "exists (select 1 from UserScrap where user_id = :userId and post_id = :postId) as isUserScraped;";
        Map<String, Object> param = Map.of("userId", userId, "postId", postId);
        return jdbcTemplate.queryForObject(sql, param, (rs, rowNum) -> {
            List<Boolean> result = new ArrayList<>();
            result.add(rs.getBoolean("isPostLikes"));
            result.add(rs.getBoolean("isPostHates"));
            result.add(rs.getBoolean("isUserScraped"));
            return result;
        });
    }

    public boolean imageExists(long postId) {
        String sql = "select has_image = '있음' from Post where post_id = :postId;";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Map.of("postId", postId), boolean.class));
    }

    public Long getCountOfComments(long postId) {
        String sql = "select count(*) from Comment where post_id = :postId and parent_id = 0 and status = 'active';";
        return jdbcTemplate.queryForObject(sql, Map.of("postId", postId), Long.class);
    }

    public List<CommentInfo> getTimeStandCommentsByPostId(long postId) {
        String sql = "select c.comment_id as parent_comment_id, u.nickname as parent_writer, u.user_id as writer_id, " +
                "u.profile_image_url as parent_image_url, c.sentences as parent_comment, c.parent_id as parent_id, " +
                "c.created_at as parent_time, c.likes as parent_likes, c.hates as parent_hates " +
                "from Comment c join User u on c.user_id = u.user_id " +
                "where c.post_id = :postId and c.parent_id = 0 and c.status = 'active' " +
                "order by c.created_at desc limit 10;";
        Map<String, Object> param = Map.of("postId", postId);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> {
            CommentInfo parentComment = new CommentInfo
                    (
                            rs.getLong("parent_comment_id"),
                            rs.getString("parent_writer"),
                            rs.getLong("writer_id"),
                            rs.getString("parent_image_url"),
                            rs.getString("parent_comment"),
                            rs.getLong("parent_id"),
                            rs.getTimestamp("parent_time"),
                            rs.getLong("parent_likes"),
                            false,
                            rs.getLong("parent_hates"),
                            false,
                            true
                    );
            return parentComment;
        });
    }

    public List<CommentInfo> getLikeStandCommentsByPostId(long postId) {
        String sql = "select c.comment_id as parent_comment_id, u.nickname as parent_writer, u.user_id as writer_id, " +
                "u.profile_image_url as parent_image_url, c.sentences as parent_comment, c.parent_id as parent_id, " +
                "c.created_at as parent_time, c.likes as parent_likes, c.hates as parent_hates " +
                "from Comment c join User u on c.user_id = u.user_id " +
                "where c.post_id = :postId and c.parent_id = 0 and c.status = 'active' " +
                "order by c.likes desc limit 10;";
        Map<String, Object> param = Map.of("postId", postId);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> {
            CommentInfo parentComment = new CommentInfo
                    (
                            rs.getLong("parent_comment_id"),
                            rs.getString("parent_writer"),
                            rs.getLong("writer_id"),
                            rs.getString("parent_image_url"),
                            rs.getString("parent_comment"),
                            rs.getLong("parent_id"),
                            rs.getTimestamp("parent_time"),
                            rs.getLong("parent_likes"),
                            false,
                            rs.getLong("parent_hates"),
                            false,
                            true
                    );
            return parentComment;
        });
    }

    public List<CommentInfo> getChildCommentsWithPrefer(long userId, List<Long> parentIds) {
        String sql = "SELECT c.comment_id AS child_comment_id, u.nickname AS child_writer, u.user_id as writer_id, " +
                "u.profile_image_url AS child_image_url, c.sentences AS child_comment, c.parent_id AS parent_id, " +
                "c.created_at AS child_time, c.likes AS child_likes, c.hates AS child_hates " +
                "FROM Comment c " +
                "JOIN User u ON c.user_id = u.user_id " +
                "WHERE c.parent_id IN (:parentIds) AND c.status = 'active' " +
                "ORDER BY c.created_at ASC";
        MapSqlParameterSource param = new MapSqlParameterSource("parentIds", parentIds);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> {
            return new CommentInfo
                    (
                            rs.getLong("child_comment_id"),
                            rs.getString("child_writer"),
                            rs.getLong("writer_id"),
                            rs.getString("child_image_url"),
                            rs.getString("child_comment"),
                            rs.getLong("parent_id"),
                            rs.getTimestamp("child_time"),
                            rs.getLong("child_likes"),
                            false,
                            rs.getLong("child_hates"),
                            false,
                            false
                    );
        });
    }

    public List<CommentInfo> getChildCommentsWithoutPrefer(List<Long> parentIds) {
        String sql = "SELECT c.comment_id AS child_comment_id, u.nickname AS child_writer, u.user_id as writer_id, " +
                "u.profile_image_url AS child_image_url, c.sentences AS child_comment, c.parent_id AS parent_id, " +
                "c.created_at AS child_time, c.likes AS child_likes, c.hates AS child_hates " +
                "FROM Comment c " +
                "JOIN User u ON c.user_id = u.user_id " +
                "WHERE c.parent_id IN (:parentIds) AND c.status = 'active' " +
                "ORDER BY c.created_at ASC";
        MapSqlParameterSource param = new MapSqlParameterSource("parentIds", parentIds);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> {
            return new CommentInfo
                    (
                    rs.getLong("child_comment_id"),
                    rs.getString("child_writer"),
                    rs.getLong("writer_id"),
                    rs.getString("child_image_url"),
                    rs.getString("child_comment"),
                    rs.getLong("parent_id"),
                    rs.getTimestamp("child_time"),
                    rs.getLong("child_likes"),
                    false,
                    rs.getLong("child_hates"),
                    false,
                    false
            );
        });
    }

    public Map<Long, Boolean> getCommentLikesHistory(long userId, List<Long> CommentIds) {
        String sql = "select comment_id from CommentLikes where user_id = :userId and comment_id in (:commentIds);";
        Map<String, Object> param = Map.of("userId", userId, "commentIds", CommentIds);
        return  jdbcTemplate.query(sql, param, (rs, rowNum) -> rs.getLong("comment_id"))
                .stream()
                .collect(Collectors.toMap(commentId -> commentId, commentId -> true));
    }

    public Map<Long, Boolean> getCommentHatesHistory(long userId, List<Long> parentCommentIds) {
        String sql = "select comment_id from CommentHates where user_id = :userId and comment_id in (:commentIds);";
        Map<String, Object> param = Map.of("userId", userId, "commentIds", parentCommentIds);
        return  jdbcTemplate.query(sql, param, (rs, rowNum) -> rs.getLong("comment_id"))
                .stream()
                .collect(Collectors.toMap(commentId -> commentId, commentId -> true));
    }
}
